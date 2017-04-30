package br.estacio.cadastrodeclientes.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.estacio.cadastrodeclientes.ImageCircle;
import br.estacio.cadastrodeclientes.R;
import br.estacio.cadastrodeclientes.model.Cliente;

/**
 * Created by Marcello on 30/04/2017.
 */

public class RVClienteAdapter extends RecyclerView.Adapter<RVClienteAdapter.ViewHolder>{

    private List<Cliente> list;
    private Activity activity;

    public RVClienteAdapter(Activity activity, List<Cliente> list) {
        this.activity = activity;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeCliente, txtFoneCliente, txtMailCliente;
        ImageView foto;
        public ViewHolder(View view){
            super(view);
            foto = (ImageView) view.findViewById(R.id.itemFoto);
            txtNomeCliente = (TextView) view.findViewById(R.id.txtNomeCliente);
            txtMailCliente = (TextView) view.findViewById(R.id.txtMailCliente);
            txtFoneCliente = (TextView) view.findViewById(R.id.txtFoneCliente);
        }
    }

    @Override
    public RVClienteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cliente cliente = getItem(position);
        holder.txtNomeCliente.setText(cliente.getNome());
        holder.txtMailCliente.setText(cliente.getEmail());
        holder.txtFoneCliente.setText(cliente.getFone());
        Bitmap bm;
        if (cliente.getCaminhoFoto() != null) {
            bm = BitmapFactory.decodeFile(cliente.getCaminhoFoto());
        }
        else {
            bm = BitmapFactory.decodeResource(activity.getResources(),
                    R.mipmap.ic_no_image);
        }
        holder.foto.setImageBitmap(ImageCircle.crop(bm));
    }

    public Cliente getItem(int position) {
        return list.get(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
