package com.example.tfc_amb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tfc_amb.Modelos.CompraRealizada;
import com.example.tfc_amb.Modelos.Direccion;
import com.example.tfc_amb.Modelos.Usuario;
import com.example.tfc_amb.Modelos.Categorias;
import com.example.tfc_amb.Modelos.Producto;
import com.example.tfc_amb.Modelos.ProductoCarrito;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ConexionDB {
    private String userID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context context;

    public ConexionDB(String userID, FirebaseFirestore db, FirebaseAuth mAuth, Context context) {
        this.userID = userID;
        this.db = db;
        this.mAuth = mAuth;
        this.context = context;
    }

    public void cargarDireccion(OnDireccionCargadaListener listener) {
        FirebaseAuth.getInstance();

        db.collection("direcciones").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Direccion direccionUsuario = documentSnapshot.toObject(Direccion.class);
                        listener.onDireccionCargada(direccionUsuario);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error obteniendo la dirección", e);
                        listener.onDireccionCargada(null);
                    }
                });
    }

    public void guardarDireccion(Direccion direccionUsuario) {
        if(direccionUsuario != null && mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("direcciones").document(userID)
                    .set(direccionUsuario)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("Firestore", "Direccion guardada correctamente");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error guardando la dirección", e);
                        }
                    });
        }
    }

    public void cargarUsuario(OnUsuarioCargadoListener listener) {
        db.collection("usuarios").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        listener.onUsuarioCargado(usuario);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onUsuarioCargado(null);
                        Toast.makeText(context, context.getText(R.string.error_obtener_datos), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void guardarUsuario(Usuario usuario) {
        db.collection("usuarios").document(userID)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("Firestore", "Usuario guardado correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error añ guardar usuario", e);
                    }
                });
    }

    public void cargarCarrito(OnCarritoCargadoListener listener) {
        db.collection("carrito").document(userID)
                .collection("productos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", "Error al obtener productos del carrito", error);
                            return;
                        }

                        List<ProductoCarrito> listaProductoCarrito = new ArrayList<>();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ProductoCarrito productoCarrito = doc.toObject(ProductoCarrito.class);
                            listaProductoCarrito.add(productoCarrito);
                        }

                        Log.d("Firestore", "Productos en carrito: " + listaProductoCarrito.size());
                        listener.onCarritoCargado(listaProductoCarrito);
                    }
                });
    }

    public void borrarCarrito(){
        db.collection("carrito").document(userID)
                .collection("productos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot producto : queryDocumentSnapshots){
                            producto.getReference().delete();
                        }
                        Log.d("Firestore", "Carrito borrado correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al borrar carrito "+e);
                    }
                });
    }

    public void realizarCompra(List<ProductoCarrito> listaProductoCarrito, double precioCarrito, double gastoEnvio, Direccion direccion, Usuario user, OnCompraRealizadaListener listener){
        Date fecha = new Date();
        CompraRealizada compra = new CompraRealizada(new ArrayList<>(listaProductoCarrito), fecha, precioCarrito, gastoEnvio, false, direccion, user);

        //para evitar problemas con la existencia del documento y que en firebase aparezca en cursiva (como si no existiera)
        //creo primero un objeto vacio y despues añado la informacion
        db.collection("compra_realizada").document(userID)
                .set(new HashMap<>())
                .addOnSuccessListener(aVoid -> {
                    // Una vez creado el documento, agrego la subcolección
                    db.collection("compra_realizada").document(userID)
                            .collection("compras")
                            .add(compra)
                            .addOnSuccessListener(documentReference -> {
                                listener.onCompraRealizada();
                                Toast.makeText(context, context.getString(R.string.compra_realizada), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Log.d("Firestore", "Error al realizar compra." ));
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error al crear documento principal."));


    }

    public void cargarComprasRealizadas(OnComprasRealizadasCargadasListener listener){
        db.collection("compra_realizada").document(userID)
                .collection("compras")
                .orderBy("fechaCompra", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<CompraRealizada> listaComprasRealizadas = new ArrayList<>();

                        for(QueryDocumentSnapshot compra : queryDocumentSnapshots){
                            CompraRealizada compraRealizada = compra.toObject(CompraRealizada.class);
                            listaComprasRealizadas.add(compraRealizada);
                        }

                    Log.d("Firestore", "Número de compras realizadas: " + listaComprasRealizadas.size());
                    listener.OnComprasRealizadasCargadas(listaComprasRealizadas);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al obtener las compras realiadas");
                    }
                });
    }

    public void cargarTodasLasComprasRealizadas(OnComprasRealizadasCargadasAdminListener listener){
        List<CompraRealizada> listaComprasRealizadas = new ArrayList<>();

        db.collection("compra_realizada")
                .get()
                .addOnSuccessListener(usuariosSnapshot -> {
                    if (usuariosSnapshot.isEmpty()) {
                        Log.d("Firestore", "No hay usuarios con compras.");
                        listener.OnComprasRealizadasCargadasAdmin(listaComprasRealizadas);
                        return;
                    }

                    for (DocumentSnapshot usuarioDoc : usuariosSnapshot.getDocuments()) {
                        String userId = usuarioDoc.getId();

                        db.collection("compra_realizada").document(userId)
                                .collection("compras")
                                .orderBy("fechaCompra", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener(comprasSnapshot -> {
                                    for (QueryDocumentSnapshot compraDoc : comprasSnapshot) {
                                        CompraRealizada compraRealizada = compraDoc.toObject(CompraRealizada.class);
                                        listaComprasRealizadas.add(compraRealizada);
                                    }
                                    listener.OnComprasRealizadasCargadasAdmin(listaComprasRealizadas);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al cargar compras del usuario.");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener usuarios.");
                    listener.OnComprasRealizadasCargadasAdmin(listaComprasRealizadas);
                });
    }

    public void actualizarCompraProcesada(String idUsuario, Date fechaCompra,  boolean compraProcesada) {
        db.collection("compra_realizada").document(idUsuario)
                .collection("compras")
                .whereEqualTo("fechaCompra", fechaCompra)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            document.getReference()
                                    .update("compraProcesada", compraProcesada);

                        }
                    } else {
                        Log.d("Firestore", "No se encontró ninguna compra con la fecha especificada.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar la compra por fecha.", e));
    }


    public void obtenerCategorias(OnCategoriasObtenidasListener listener) {
        db.collection("categorias")
                .orderBy("titulo", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        if (value == null) return;

                        // Limpiamos la lista antes de actualizarla para evitar duplicados
                        List<Categorias> listaCategorias = new ArrayList<>();

                        // Recorremos los documentos y los añadimos a la lista
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Categorias categoria = doc.toObject(Categorias.class);
                            listaCategorias.add(categoria);
                        }

                        // Log para comprobar el resultado
                        Log.d("Firestore Categorias", "Categorías actualizadas: " + listaCategorias.size());

                        // Notificar al adaptador que los datos cambiaron
                        listener.onCategoriasObtenidas(listaCategorias);
                    }
                });
    }

    public void cargarFavoritos(String userID, OnFavoritosCargadosListener listener) {
        //Accedemos a la coleccion usuario, y dentro de ella, a la colleccion favoritos
        db.collection("usuarios").document(userID)
                .collection("favoritos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }
                        if (value == null) return;

                        List<Integer> idsProductos = new ArrayList<>();

                        // Obtenemos los objetos o modificamos segun los cambios
                        for (DocumentChange producto : value.getDocumentChanges()) {
                            String idProducto = producto.getDocument().getId();
                            switch (producto.getType()) {
                                case ADDED:
                                    //Si se ha añadido a la lista favoritos un producto lo añadimos la lista
                                    //para buscar sus datos posteriormente
                                    idsProductos.add(Integer.parseInt(idProducto));
                                    break;
                                case REMOVED:
                                    //En el caso de que se haya eliminado un producto de la coleccion favoritos lo eliminamos
                                    //de la list para que no lo muestre en el recyclerview
                                    listener.onProductoEliminado(idProducto);
                                    break;
                            }
                        }
                        if(!idsProductos.isEmpty()){
                            obtenerProductoPorListId(idsProductos, listener);
                        }
                    }
                });

    }
    public void obtenerProductoPorListId(List<Integer> idsProducto, OnFavoritosCargadosListener listener) {

        List<Producto> listaProductos = new ArrayList<>();

        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Convertimos cada documento en un objeto Producto
                            Producto producto = documentSnapshot.toObject(Producto.class);
                            if (idsProducto.contains(producto.getId())) {
                                listaProductos.add(producto);
                            }
                        }
                    }
                    // Notificamos los cambios
                    listener.OnFavoritosCargados(listaProductos);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener productos mediante id", e));
    }


    public void anadirFavoritos(Producto producto, OnFavoritoAnadidoListener listener){
        db.collection("usuarios").document(userID)
                .collection("favoritos")
                .document(String.valueOf(producto.getId())) // Aquí usamos el idProducto
                .set(producto)  // Setea el producto en el documento existente o lo crea si no existe
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.OnFavoritoAnadido();
                        Log.d("Firebase", "anadirFavoritos correcto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase", "anadirFavoritos error");
                    }
                });
    }

    public void eliminarFavorito(Producto producto, OnFavoritoEliminadoListener listener){
        db.collection("usuarios").document(userID)
                .collection("favoritos")
                .document(String.valueOf(producto.getId()))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.OnFavoritoEliminado();
                        Log.d("Firebase", "eliminarFavorito correcto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase", "eliminarFavorito error");
                    }
                });
    }

    public void anadirACarrito(ProductoCarrito producto, OnProductoAnadidoACarritoListener listener){
        db.collection("carrito").document(userID)
                .collection("productos")
                .document(String.valueOf(producto.getId()))
                .set(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.OnProductoAnadidoACarrito();
                        Log.d("Firebase", "anadirACarrito correcto");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase", "anadirACarrito error");
                    }
                });
    }

    public void eliminarProductoCarrito(int idProducto, String userID) {
        db.collection("carrito").document(userID)
                .collection("productos")
                .document(String.valueOf(idProducto))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("ProductosCarritoAdapter", "Producto eliminado del carrito correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ProductosCarritoAdapter", "Error al eliminar producto del carrito");
                    }
                });
    }

    public void actualizarCantidadCarrito(int idProducto, int cantidad){
        db.collection("carrito").document(userID)
                .collection("productos")
                .document(String.valueOf(idProducto))
                .update("cantidadComprada", cantidad)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firebase", "Cantidad en carrito actualizada");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase", "Error al actualizar cantidad");
                    }
                });
    }

    public void verificarProductoFavorito(int idProducto, OnVerificarProductoFavoritoListener listener) {
        if (idProducto != -1) {
            // Consultamos Firestore para comprobar si el producto está en favoritos
            db.collection("usuarios").document(userID)
                    .collection("favoritos")
                    .document(String.valueOf(idProducto))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Si esta en favoritos devolvemos un true
                            listener.OnVerificarProductoFavorito(true);
                        } else {
                            // Si no está en favoritos devolvemos un false
                            listener.OnVerificarProductoFavorito(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("Firebase", "verificarProductoFavorito error");
                    });
        }
    }

    public void cargarProductos(OnCargarProductosListener listener){

        db.collection("productos")
                .orderBy("titulo", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        if (value == null) return;

                        // Limpiamos la lista antes de actualizarla para evitar duplicados
                        ArrayList<Producto> listaProductosDB = new ArrayList<Producto>();

                        // Recorremos los documentos y los añadimos a la lista
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Producto producto = doc.toObject(Producto.class);
                            listaProductosDB.add(producto);
                        }

                        // Notificar al adaptador que los datos cambiaron
                        listener.OnCargarProductos(listaProductosDB);
                    }
                });
    }

    public void obtenerProductoPorId(int productoID, OnObtenerProductoPorIdListener listener) {

        db.collection("productos")
                .whereEqualTo("id", productoID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Producto producto = document.toObject(Producto.class);
                        listener.OnObtenerProductoPorId(producto);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener productos por categoría", e);
                });

    }

    //Mediante este metodo primero buscamos el producto mediante su ID, le restamos la cantidad comprada
    //a la cantidad de existencias que habian y se suman las cantidades vendidads
    public void actualizarCantidadProducto(int productoID, int cantidadComprada, OnActualizarCantidadProductoListener listener){

        db.collection("productos")
                .whereEqualTo("id", productoID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Producto producto = document.toObject(Producto.class);

                        if (producto != null) {
                            // calculamos las nuevas cantidades
                            int nuevaCantidad = producto.getCantidad() - cantidadComprada;
                            int nuevaCantidadVendida = producto.getCantidadVendida() + cantidadComprada;

                            // Actualizamos los campos en Firestore
                            document.getReference()
                                    .update("cantidad", nuevaCantidad, "cantidadVendida", nuevaCantidadVendida)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Cantidad de producto actualizada correctamente");
                                            listener.OnActualizarCantidadProducto();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Firestore", "Error al actualizar la cantidad");
                                        }
                                    });
                        }
                    } else {
                        Log.d("Firestore", "Producto no encontrado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al actualizar la cantidad");
                    }
                });
    }

    public void actualizarCategoria(String id, String titulo, String url){
        db.collection("categorias").document(id)
                .update("titulo", titulo, "urlFoto", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Categoria actualizada correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al actualizar la categoria");
                    }
                });
    }

    public void eliminarCategoria(String id, String categoria){
        db.collection("categorias").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("productos")
                                .whereEqualTo("categoria", categoria)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            documentSnapshot.getReference().delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("Firebase", "Producto eliminado correctamente");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Firebase", "Error al eliminar producto");
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al eliminar la categoria");
                    }
                });
    }

    public void crearCategoria(Categorias categoria){
        db.collection("categorias").document(String.valueOf(categoria.getId()))
                .set(categoria)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Categoria creada correctamente");
                        Toast.makeText(context, context.getString(R.string.categoria_creada), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al crear categoria");
                    }
                });
    }

    public void actualizarProducto(Producto producto){

        db.collection("productos")
                .document(String.valueOf(producto.getId()))
                .update(
                        "titulo", producto.getTitulo(),
                        "cantidad", producto.getCantidad(),
                        "cantidadVendida", producto.getCantidadVendida(),
                        "precio", producto.getPrecio(),
                        "urlFoto", producto.getUrlFoto(),
                        "categoria", producto.getCategoria()
                )
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", "Producto actualizado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al actualizar el producto", e);
                });
    }

    public void eliminarProductoPorId(String productoId){
        db.collection("productos")
                .document(productoId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Producto eliminado correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al guardar lista con producto eliminado");
                    }
                });

    }

    public void crearProducto(Producto productoNuevo){

        //Primero verificamos si el id ya existe haciendo un get, si no encontramos un documento con ese id
        //procedemos a guardar el producto. En cambio, si encontramos un documento con ese id lanzamos un toast
        //indicando que ya existe un producto con ese id para evitar que se sobreescriba.
        db.collection("productos")
                .document(String.valueOf(productoNuevo.getId()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Si no existe, lo creamos
                        db.collection("productos")
                                .document(String.valueOf(productoNuevo.getId()))
                                .set(productoNuevo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Firestore", "Producto creado correctamente");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Firestore", "Error al crear producto");
                                    }
                                });
                    } else {
                        // Si ya existe, mostramos un mensaje de error
                        Log.d("Firestore", "El producto con este ID ya existe");
                        Toast.makeText(context, context.getString(R.string.error_id_producto), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error al buscar categoria para crear nuevo producto");
                    }
                });
    }

    public void obtenerProductosPorCategoria(String categoria, OnObtenerProductosDeCategoriaListener listener){
        List<Producto> listaProductos = new ArrayList<>();

        db.collection("productos")
                .whereEqualTo("categoria", categoria)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        listaProductos.add(producto);
                    }
                    listener.OnObtenerProductosDeCategoria(listaProductos);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener productos por categoría", e);
                });
    }

    public void obtenerURLsDeImagenes(OnURLsObtenidasListener listener) {
        List<String> listaURLs = new ArrayList<>();

        db.collection("imagenes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String urlImagen = document.getString("url");
                            if (urlImagen != null) {
                                listaURLs.add(urlImagen);
                            }
                        }
                        // Llama al listener con la lista de URLs obtenidas
                        listener.onURLsObtenidas(listaURLs);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error al obtener imágenes");
                    }
                });
    }

    // Interfaz para manejar el callback
    public interface OnDireccionCargadaListener {
        void onDireccionCargada(Direccion direccion);
    }

    public interface OnUsuarioCargadoListener {
        void onUsuarioCargado(Usuario usuario);
    }

    public interface OnCarritoCargadoListener{
        void onCarritoCargado (List<ProductoCarrito> listaProductoCarrito);
    }

    public interface OnCompraRealizadaListener{
        void onCompraRealizada();
    }

    public interface OnCategoriasObtenidasListener{
        void onCategoriasObtenidas(List<Categorias> listaCategorias);
    }

    public interface OnComprasRealizadasCargadasListener{
        void OnComprasRealizadasCargadas(List<CompraRealizada> listaComprasRealizadas);
    }

    public interface OnComprasRealizadasCargadasAdminListener{
        void OnComprasRealizadasCargadasAdmin(List<CompraRealizada> listaComprasRealizadas);
    }

    public interface OnFavoritosCargadosListener{
        void OnFavoritosCargados(List<Producto> listaProductos);
        void onProductoEliminado(String idProducto);
    }

    public interface OnFavoritoAnadidoListener{
        void OnFavoritoAnadido();
    }

    public interface OnFavoritoEliminadoListener{
        void OnFavoritoEliminado();
    }

    public interface OnProductoAnadidoACarritoListener{
        void OnProductoAnadidoACarrito();
    }

    public interface OnVerificarProductoFavoritoListener{
        void OnVerificarProductoFavorito(boolean esFavorito);
    }

    public interface OnCargarProductosListener{
        void OnCargarProductos(List<Producto> listaProductosDB);
    }

    public interface OnObtenerProductoPorIdListener{
        void OnObtenerProductoPorId(Producto productoDB);
    }

    public interface OnActualizarCantidadProductoListener{
        void OnActualizarCantidadProducto();
    }

    public interface OnObtenerProductosDeCategoriaListener{
        void OnObtenerProductosDeCategoria(List<Producto> listaProductos);
    }

    public interface OnURLsObtenidasListener {
        void onURLsObtenidas(List<String> listaURLs);
    }
}
