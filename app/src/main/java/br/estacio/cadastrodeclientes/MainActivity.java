package br.estacio.cadastrodeclientes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.estacio.cadastrodeclientes.adapter.ClienteAdapater;
import br.estacio.cadastrodeclientes.adapter.RVClienteAdapter;
import br.estacio.cadastrodeclientes.dao.ClienteDAO;
import br.estacio.cadastrodeclientes.model.Cliente;

public class MainActivity extends AppCompatActivity{

    private RecyclerView listaCliente;
    private RVClienteAdapter adapter;
    private List<Cliente> clientes;

    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCliente = (RecyclerView) findViewById(R.id.listaCliente);
        listaCliente.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaCliente.setLayoutManager(layoutManager);

        adapter = new RVClienteAdapter(this, clientes);
        listaCliente.setAdapter(adapter);

        Button btnNovoAluno = (Button) findViewById(R.id.btnNovoAluno);
        btnNovoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        });

        //registerForContextMenu(listaCliente);
        initSwipe();

    }

    private void initSwipe(){
        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT){
                    confirmaRemocao(adapter.getItem(position));
                } else {
                    Cliente clienteSelecionado = adapter.getItem(position);
                    Intent cadCliente = new Intent(MainActivity.this, ClienteActivity.class);
                    cadCliente.putExtra("clienteSelecionado", clienteSelecionado);
                    startActivity(cadCliente);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(listaCliente);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }


    private void carregaLista() {
        ClienteDAO dao = new ClienteDAO(this);
        clientes = dao.list();
        dao.close();

        adapter = new RVClienteAdapter(this, clientes);
        listaCliente.setAdapter(adapter);
    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Cliente clienteSelecionado = adapter.getItem(info.position);
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                confirmaRemocao(clienteSelecionado);
                return false;
            }
        });
    }
    */

    private void confirmaRemocao(final Cliente clienteSelecionado) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Remoção");
            dialogBuilder.setMessage(
                    String.format("Confirma a remoção do cliente %s?",
                            clienteSelecionado.getNome()));
            dialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ClienteDAO dao = new ClienteDAO(MainActivity.this);
                    dao.delete(clienteSelecionado.getId());
                    dao.close();
                    carregaLista();
                    dialog.cancel();
                }
            });
            dialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    carregaLista();
                    dialog.cancel();
                }
            });
            dialogBuilder.create().show();
    }
}
