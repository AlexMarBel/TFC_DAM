package com.example.tfc_amb;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUploader {

    private final FirebaseFirestore db;

    public FirestoreUploader() {
        db = FirebaseFirestore.getInstance();
    }

    public void subirCategorias() {
        subirCategoria(1, "Cebollas", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/cebollaRec.jpg?alt=media&token=4134087d-6c31-4c64-bd71-add6eced66ae", crearProductosFrutas());
        //subirCategoria(4, "Manzanas", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/cebollaRec.jpg?alt=media&token=4134087d-6c31-4c64-bd71-add6eced66ae", crearProductosVerduras());
        //subirCategoria(2, "Pepinos", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/pepinoRec.jpg?alt=media&token=2f639169-9e0f-4dd5-9843-a1a698abdd77", crearProductos3());
    }

    private void subirCategoria(int id, String nombre, String imagen, List<Map<String, Object>> productos) {
        Map<String, Object> categoria = new HashMap<>();
        categoria.put("id", id);
        categoria.put("titulo", nombre);
        categoria.put("urlFoto", imagen);
        categoria.put("productos", productos);

        db.collection("Category").document(String.valueOf(id))
                .set(categoria)
                .addOnSuccessListener(unused -> Log.d("FirestoreUploader", "Categoría " + nombre + " subida con éxito"))
                .addOnFailureListener(e -> Log.e("FirestoreUploader", "Error al subir la categoría " + nombre, e));
    }

    private List<Map<String, Object>> crearProductosFrutas() {
        List<Map<String, Object>> productos = new ArrayList<>();
        productos.add(crearProducto(101, "Cebolla", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/cebollasNormal.jpg?alt=media&token=67174572-0733-46a8-84b0-bd3fbca0729f", 2.20, 100, 40));
        //productos.add(crearProducto(102, "Cebolla morada", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/cebollasMorada.jpg?alt=media&token=259a3b3a-9052-4d8b-b11c-1e74e831f084", 2.10, 150, 37));
        //productos.add(crearProducto(103, "Cebolla tierna", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/cebollasTierna.jpg?alt=media&token=bea6ad05-f01d-4fd2-bf98-5d4785b66f59", 2.50, 150, 12));
        //productos.add(crearProducto(104, "Pimiento italiano", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/pimientoItaliano.jpg?alt=media&token=229e6431-04f1-4c00-9f52-0f61c3804d9d", 1.99, 100, 9));

        return productos;
    }

    private List<Map<String, Object>> crearProductosVerduras() {
        List<Map<String, Object>> productos = new ArrayList<>();
        productos.add(crearProducto(401, "Manzana Granny Smith", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/manzanaSmith.jpg?alt=media&token=89a09554-01cd-4ea8-bab9-5b9d3ec8f558", 1.50, 200, 55));
        productos.add(crearProducto(402, "Manzana roja", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/manzanaRoja.jpg?alt=media&token=90087cb4-c323-4b04-af02-3a1496896aac", 1.20, 200, 32));
        productos.add(crearProducto(403, "Manzana Golden", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/manzanaGolden.jpg?alt=media&token=538484c6-7f39-458c-b275-14e830ad0e15", 0.8, 200, 20));
        //productos.add(crearProducto(604, "Zanahoria", "url_foto_zanahoria", 0.8, 200, 20));
        return productos;
    }

    private List<Map<String, Object>> crearProductos3() {
        List<Map<String, Object>> productos = new ArrayList<>();
        productos.add(crearProducto(201, "Pepino inglés largo", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/pepinoLargo.jpg?alt=media&token=90cd5877-8f61-44a5-9308-cad985b40df6", 1.30, 100, 7));
        productos.add(crearProducto(202, "Pepino blanco", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/tomateBuey.jpg?alt=media&token=25871839-2e9b-49bb-bdda-0af084491d63", 1.50, 150, 23));
        productos.add(crearProducto(203, "Pepinillo", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/pepinoBlanco.jpg?alt=media&token=dac6ca9f-1f07-4a91-9916-286b608e7876", 0.99, 150, 15));
        //productos.add(crearProducto(504, "Tomate amarillo", "https://firebasestorage.googleapis.com/v0/b/ambtfc.firebasestorage.app/o/tomateAmarillo.jpg?alt=media&token=46e95923-609e-4ea2-9ecc-6ab1bf814083", 2.40, 10, 0));

        return productos;
    }

    private Map<String, Object> crearProducto(int id, String nombre, String foto, double precio, int cantidad, int cantidadVendida) {
        Map<String, Object> producto = new HashMap<>();
        producto.put("id", id);
        producto.put("titulo", nombre);
        producto.put("urlFoto", foto);
        producto.put("precio", precio);
        producto.put("cantidad", cantidad);
        producto.put("cantidadVendida", cantidadVendida);
        return producto;
    }
}
