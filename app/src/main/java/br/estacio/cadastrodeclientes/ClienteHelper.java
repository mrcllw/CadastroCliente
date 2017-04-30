package br.estacio.cadastrodeclientes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.estacio.cadastrodeclientes.dao.ClienteDAO;
import br.estacio.cadastrodeclientes.model.Cliente;

/**
 * Created by carlos on 27/03/17.
 */

public class ClienteHelper {

    private ClienteActivity activity;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private EditText edtNome, edtMail, edtFone, edtCEP, edtEndereco,
            edtNumero, edtCidade, edtDataNasc;
    private RadioGroup rgSexo;
    private Button btnSalvarCliente, btnFoto;
    private ImageView foto;

    private Cliente cliente;

    public ClienteHelper(final ClienteActivity activity) {
        this.activity = activity;
        edtNome = (EditText) activity.findViewById(R.id.edtNome);
        edtDataNasc = (EditText) activity.findViewById(R.id.edtDataNasc);
        edtDataNasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(v);
            }
        });
        edtMail = (EditText) activity.findViewById(R.id.edtMail);
        edtFone = (EditText) activity.findViewById(R.id.edtFone);
        edtCEP = (EditText) activity.findViewById(R.id.edtCEP);
        edtEndereco = (EditText) activity.findViewById(R.id.edtEndereco);
        edtNumero = (EditText) activity.findViewById(R.id.edtNumero);
        edtCidade = (EditText) activity.findViewById(R.id.edtCidade);
        rgSexo = (RadioGroup) activity.findViewById(R.id.rgSexo);
        btnSalvarCliente = (Button) activity.findViewById(R.id.btnSalvarCliente);
        btnSalvarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cliente = carregaDadosDaTela();
                if (validate()) {
                    ClienteDAO dao = new ClienteDAO(activity);
                    if (cliente.getId() == 0) {
                        dao.insert(cliente);
                    } else {
                        dao.update(cliente);
                    }
                    dao.close();
                    activity.finish();
                }
            }
        });
        foto = (ImageView) activity.findViewById(R.id.foto);
        btnFoto = (Button) activity.findViewById(R.id.formFotoButton);

        cliente = (Cliente) activity.getIntent().getSerializableExtra("clienteSelecionado");
        if (cliente != null) {
            carregaDadosParaTela(cliente);
        }
        else {
            cliente = new Cliente();
        }
    }

    public Cliente carregaDadosDaTela() {
        cliente.setNome(edtNome.getText().toString());
        cliente.setEmail(edtMail.getText().toString());
        cliente.setFone(edtFone.getText().toString());
        cliente.setCEP(edtCEP.getText().toString());
        cliente.setEndereco(edtEndereco.getText().toString());
        cliente.setNumero(edtNumero.getText().toString());
        cliente.setCidade(edtCidade.getText().toString());
        cliente.setSexo(rgSexo.getCheckedRadioButtonId() == R.id.feminino ? 0 : 1);
        cliente.setCaminhoFoto((String) foto.getTag());
        cliente.setDataNasc(getDate());
        return cliente;
    }

    public void carregaDadosParaTela(Cliente cliente) {
        this.cliente = cliente;
        edtNome.setText(cliente.getNome());
        edtMail.setText(cliente.getEmail());
        edtFone.setText(cliente.getFone());
        edtCEP.setText(cliente.getCEP());
        edtEndereco.setText(cliente.getEndereco());
        edtNumero.setText(cliente.getNumero());
        edtCidade.setText(cliente.getCidade());
        rgSexo.check(cliente.getSexo() == 0 ? R.id.feminino : R.id.masculino);
        setImage(cliente.getCaminhoFoto());
        setDate(cliente.getDataNasc());
    }

    public boolean validate() {
        boolean valid = true;
        if (edtNome.getText().toString().trim().isEmpty()) {
            edtNome.setError("Campo nome é obrigatório!");
            valid = false;
        }
        if (edtDataNasc.getText().toString().trim().isEmpty()) {
            edtDataNasc.setError("Campo data de nascimento é obrigatório!");
            valid = false;
        }
        if (edtMail.getText().toString().trim().isEmpty()) {
            edtMail.setError("Campo e-mail é obrigatório!");
            valid = false;
        }
        return valid;
    }

    public Button getBtnFoto() {
        return btnFoto;
    }

    public void setImage(String localArquivoFoto) {
        if (localArquivoFoto != null) {
            Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
            Bitmap imagemFotoReduzida = Bitmap
                    .createScaledBitmap(imagemFoto, imagemFoto.getWidth(),
                            300, true);
            foto.setImageBitmap(imagemFotoReduzida);
            foto.setTag(localArquivoFoto);
            foto.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    private void setDate(Calendar calendar) {
        try {
            edtDataNasc.setText(dateFormat.format(calendar.getTime()));
        }
        catch (Exception e) {
            edtDataNasc.setText(dateFormat.format(new Date()));
        }
    }

    private Calendar getDate() {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(edtDataNasc.getText().toString()));
        }
        catch (Exception e) {
            c.setTime(new Date());
        }
        return c;
    }

    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable("calendar", cliente.getDataNasc());
        fragment.setArguments(args);
        fragment.show(activity.getFragmentManager(), "");
    }

}
