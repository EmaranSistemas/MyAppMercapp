package com.example.bottomnavigationwithdrawermenu.Mercaderista;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bottomnavigationwithdrawermenu.MainActivity;
import com.example.bottomnavigationwithdrawermenu.Mercaderista.Adapters.SummaryAdapter;
import com.example.bottomnavigationwithdrawermenu.Mercaderista.Entity.Summary;
import com.example.bottomnavigationwithdrawermenu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private Summary summary;
    private SummaryAdapter summaryAdapter;

    private TextView selectedDateTV;
    public static ArrayList<Summary> SumaryArrayList = new ArrayList<>();

    String url = "https://emaransac.com/android/resumen.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        retrieveData(url);
        SummaryAdapter adapter = new SummaryAdapter(this, SumaryArrayList);
        adapter.setOnItemClickListener(new SummaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Acciones a realizar cuando se hace clic en un elemento
                //Toast.makeText(SummaryActivity.this, SumaryArrayList.get(position).getTienda(), Toast.LENGTH_SHORT).show();
                showAlertDialog(SumaryArrayList.get(position).getId(), SumaryArrayList.get(position).getTienda(),SumaryArrayList.get(position).getProducto(), SumaryArrayList.get(position).getInventario(), SumaryArrayList.get(position).getPedido());
            }
        });

        summaryAdapter = adapter; // Asignar el adaptador a la variable summaryAdapter

        RecyclerView recyclerView = findViewById(R.id.recycler_view_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        FloatingActionButton btn1 = findViewById(R.id.update_btn);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SummaryActivity.this, "Actualizando...", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        selectedDateTV = findViewById(R.id.idTVSelectedDate);

        // Agrega el listener al botón

        FloatingActionButton btn2 = findViewById(R.id.btn1_cal);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene la instancia del calendario
                final Calendar c = Calendar.getInstance();

                // Obtiene el día, mes y año actual
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Crea el cuadro de diálogo de selección de fecha
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SummaryActivity.this, // Reemplaza "YourActivity" con el nombre de tu actividad
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Establece la fecha seleccionada en el TextView
                                // selectedDateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_SHORT).show();
                            }
                        },
                        year, month, day);
                // Muestra el cuadro de diálogo de selección de fecha
                datePickerDialog.show();
            }
        });
    }
    private void showAlertDialog(String id, String tienda,String nombre,String inv, String ped) {
        //Toast.makeText(SummaryActivity.this, id +" "+ nombre, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);

        SpannableStringBuilder spannableTitle = new SpannableStringBuilder(nombre);
        spannableTitle.setSpan(new RelativeSizeSpan(0.7f), 0, spannableTitle.length(), 0);

        builder.setTitle(spannableTitle);
        // Agrega los componentes necesarios al cuadro de diálogo (EditTexts, botones, etc.)
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText pedido = new EditText(this);
        EditText inventario = new EditText(this);

        //pedido.setText(ped);
        //inventario.setText(inv);

        // Establece la propiedad singleLine en true
        pedido.setSingleLine(true);
        inventario.setSingleLine(true);


        pedido.setHint("Pedido: "+ ped);
        inventario.setHint("Inventario: "+ inv);



        // Configura el tipo de entrada de texto como numérico
        pedido.setInputType(InputType.TYPE_CLASS_NUMBER);
        inventario.setInputType(InputType.TYPE_CLASS_NUMBER);


        linearLayout.addView(pedido);
        linearLayout.addView(inventario);


        builder.setView(linearLayout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inventarioValue = inventario.getText().toString();
                String pedidoValue = pedido.getText().toString();

                // Lógica para guardar los cambios realizados en el elemento seleccionado

                if(pedidoValue.isEmpty()){
                    pedidoValue = ped;
                }

                if(inventarioValue.isEmpty()){
                    inventarioValue = inv;
                }

                actualizar_reporte(id,tienda,nombre,inventarioValue,pedidoValue);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lógica para cancelar la edición del elemento seleccionado
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void retrieveData(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SumaryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            JSONArray jsonArray = jsonObject.getJSONArray("datos");
                            if (exito.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String img = object.getString("imagen");
                                    String id = object.getString("id");
                                    String tienda = object.getString("tienda");
                                    String sucursal = object.getString("sucursal");
                                    String producto = object.getString("producto");
                                    String inventario = object.getString("inventario");
                                    String pedido = object.getString("pedido");
                                    String fecha_string = object.getString("fecha");

                                    //Log.d("Retrival ", img + " " + id + " " + tienda + " " + producto + " " + inventario + " " + pedido + " " + fecha_string);
                                    summary = new Summary(img, id,tienda+" / "+sucursal, producto, inventario, pedido, fecha_string);
                                    SumaryArrayList.add(summary);
                                }
                                summaryAdapter.notifyDataSetChanged(); // Notificar cambios en el adaptador después de agregar los elementos
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SummaryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    public void actualizar_reporte(String id, String sucursal,String producto,String inventario, String pedido) {
        Log.d("los datos", id + " " + sucursal + " " + producto + " " + inventario + " " + pedido);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "https://emaransac.com/android/actualizar_reporte.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SummaryActivity.this, response, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SummaryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("sucursal", sucursal);
                params.put("producto", producto);
                params.put("inventario", inventario);
                params.put("pedido", pedido);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SummaryActivity.this);
        requestQueue.add(request);
        retrieveData(url);
    }
    /*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // Finalizar la actividad actual
        finish();
    }
    */

}