package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NasaApdater;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.DataServer;
import com.example.myapplication.model.HackNasa;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Getdata_activity extends AppCompatActivity {

    private List<HackNasa> listHackNasa;
    private NasaApdater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_from_my_server);
        initViews();
    }

    // Phương thức  sử dụng để khởi tạo các thành phần giao diện và thực hiện các tác vụ liên quan.
    private void initViews() {
        // Khởi tạo một ArrayList rỗng để chứa dữ liệu từ máy chủ.
        listHackNasa = new ArrayList<>();

        // Khởi tạo một instance của NasaApdater và liên kết nó với Activity hiện tại.
        adapter = new NasaApdater(this);

        // Thiết lập lắng nghe sự kiện nhấp vào nút "Back" (btn_back).
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gọi phương thức getData() để lấy dữ liệu từ máy chủ.
        getData();
    }

    // Phương thức sử dụng để gọi API trên máy chủ để lấy dữ liệu.
    private void getData() {
        // Gọi phương thức getData() của APIservice trên máy chủ sử dụng Retrofit.
        // Sử dụng enqueue để thực hiện cuộc gọi bất đồng bộ và xử lý kết quả gọi API thông qua đối tượng Callback<DataServer>.
        Apiserver.apiService.getData().enqueue(new Callback<DataServer>() {
            @Override
            public void onResponse(Call<DataServer> call, Response<DataServer> response) {
                // Nhận dữ liệu trả về từ cuộc gọi API và gán vào biến listHackNasa.
                listHackNasa = response.body().getData();

                // Thiết lập dữ liệu mới vào Adapter bằng cách gọi phương thức setData() của Adapter và truyền vào danh sách listHackNasa.
                adapter.setData(listHackNasa);

                // Tìm RecyclerView bằng ID và gán vào biến rcv.
                RecyclerView rcv = findViewById(R.id.rcv);

                // Đặt adapter làm Adapter cho RecyclerView.
                rcv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<DataServer> call, Throwable t) {
                // Xử lý lỗi khi không lấy được dữ liệu.
                // Thông báo lỗi có thể được xử lý ở đây.
            }
        });
    }


}
