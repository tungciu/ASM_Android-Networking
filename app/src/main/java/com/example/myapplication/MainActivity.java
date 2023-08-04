package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.myapplication.adapter.CustomAdapter;
import com.example.myapplication.api.ApiNasa;
import com.example.myapplication.api.Apiiresspot;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.HackNasa;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private HackNasa hackNasa;
    private static final String API_KEY = "SmfZWTenK7oRlmc3C7GUGhsakelJb9cDQgGNbU2i";
    private ApiNasa apiNasa;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    ImageView imgout;
    ListView listView;
    CustomAdapter customAdapter;
    private Executor executor = Executors.newFixedThreadPool(3);


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        hackNasa = new HackNasa();
        // ánh xạ và bắt sự kiện
        initViews();
        // chekc chuyển màn
        checkAndOut();
    }

    // Phương thức kiểm tra trạng thái xác thực của người dùng và xử lý chức năng đăng xuất
    private void checkAndOut() {
        // Thiết lập lắng nghe trạng thái xác thực từ Firebase
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Kiểm tra xem người dùng đã đăng nhập hay chưa
                if (firebaseAuth.getCurrentUser() == null) {
                    // Nếu chưa đăng nhập, chuyển hướng sang LoginActivity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        // Thiết lập lắng nghe sự kiện nhấp vào nút "Đăng xuất"
        imgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện đăng xuất người dùng
                mAuth.signOut();
            }
        });
    }

    // Phương thức khởi tạo các thành phần giao diện và thiết lập lắng nghe sự kiện nhấp vào
    private void initViews() {
        imgout = findViewById(R.id.btn_logout);
        mAuth = FirebaseAuth.getInstance();
        // Khởi tạo một CustomAdapter với danh sách rỗng và liên kết nó với MainActivity
        customAdapter = new CustomAdapter(this, new ArrayList<>());
        listView = findViewById(R.id.list_view);
        // Đặt customAdapter làm Adapter cho listView
        listView.setAdapter(customAdapter);

        // Thiết lập lắng nghe sự kiện nhấp vào nút "Lấy dữ liệu từ NASA"
        findViewById(R.id.btn_get_nasa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức getMultipleDaysDataFromNasa với khóa API (API_KEY) để lấy dữ liệu từ API của NASA
                getMultipleDaysDataFromNasa(API_KEY);
            }
        });

        // Thiết lập lắng nghe sự kiện nhấp vào nút "Lấy dữ liệu từ máy chủ "
        findViewById(R.id.btn_get_data_form_my_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển hướng sang hoạt động Getdata_activity để lấy dữ liệu từ máy chủ
                startActivity(new Intent(MainActivity.this, Getdata_activity.class));
            }
        });
    }

    // Phương thức  lấy dữ liệu từ API của NASA
    private void getMultipleDaysDataFromNasa(String api_key) {
        // Tạo danh sách các ngày bằng cách gọi phương thức generateDateList()
        List<String> selectedDates = generateDateList();

        // Duyệt qua từng ngày trong danh sách đã tạo
        for (String date : selectedDates) {
            // Sử dụng Executor để thực hiện các công việc lấy dữ liệu từ NASA trên các luồng riêng biệt
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Gọi phương thức callApiGetDataFormNasa để lấy dữ liệu từ API của NASA cho ngày tương ứng
                    callApiGetDataFormNasa(api_key, date);
                }
            });
        }
    }

    // Phương thức ntạo một danh sách các chuỗi đại diện cho ngày gần nhau (15 ngày gần nhau).
    private List<String> generateDateList() {
        // Khởi tạo một danh sách rỗng để chứa các chuỗi đại diện cho ngày
        List<String> dateList = new ArrayList<>();

        // Duyệt qua 15 ngày gần nhau
        for (int i = 0; i < 15; i++) {
            // Khởi tạo một đối tượng Calendar
            Calendar calendar = Calendar.getInstance();
            // Giảm giá trị của trường DAY_OF_YEAR (ngày trong năm) đi i ngày để lấy ngày của mỗi lần lặp
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            // Lấy thông tin năm, tháng và ngày từ đối tượng Calendar
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1); // Tháng bắt đầu từ 0
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            // Tạo chuỗi đại diện cho ngày dưới định dạng "yyyy-MM-dd" và thêm nó vào danh sách dateList
            String formattedDate = year + "-" + (month.length() == 1 ? "0" + month : month) + "-" + (day.length() == 1 ? "0" + day : day);
            dateList.add(formattedDate);
        }

        // Trả về danh sách dateList chứa 15 ngày gần nhau
        return dateList;
    }

    // Phương thức này được sử dụng để gọi API của NASA và lấy dữ liệu cho một ngày cụ thể.
    private void callApiGetDataFormNasa(String api_key, String date) {
        // Khởi tạo API interface để gọi API của NASA
        apiNasa = Apiiresspot.getApiNasa();

        // Gọi phương thức getDataFromNasa của API interface và truyền khóa API (api_key) và ngày (date) cần lấy dữ liệu
        apiNasa.getDataFromNasa(api_key, date).enqueue(new Callback<HackNasa>() {
            @Override
            public void onResponse(Call<HackNasa> call, Response<HackNasa> response) {
                // Trả về kết quả của cuộc gọi API thành công (onResponse)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Nhận dữ liệu trả về từ cuộc gọi API
                        hackNasa = response.body();
                        // Kiểm tra xem dữ liệu trả về có null hay không
                        if (hackNasa != null) {
                            // Nếu không null, thêm dữ liệu vào CustomAdapter và cập nhật giao diện
                            customAdapter.add(hackNasa);
                            customAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<HackNasa> call, Throwable t) {
                // Xử lý khi có lỗi trong quá trình gọi API (onFailure)
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }









}
