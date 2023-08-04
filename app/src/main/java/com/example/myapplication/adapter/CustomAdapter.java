package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.HackNasa;

import java.util.List;
import java.util.Base64;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomAdapter extends ArrayAdapter<HackNasa> {
    private HackNasa hackNasa2;
    private Context context;
    private List<HackNasa> hackNasaList;
    String base64UrlHd;
    String base64url;
    public CustomAdapter(Context context, List<HackNasa> hackNasaList) {
        super(context, 0, hackNasaList);
        this.context = context;
        this.hackNasaList = hackNasaList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_listview, parent, false);
        }

        HackNasa hackNasa = hackNasaList.get(position);
        // ánh xạ view
        TextView titleTextView = itemView.findViewById(R.id.title_text_view);
        TextView dateTextView = itemView.findViewById(R.id.date_text_view);
        TextView explanationTextView = itemView.findViewById(R.id.explanation_text_view);
        CircleImageView imageView = itemView.findViewById(R.id.image_view);
        Button btn_post=itemView.findViewById(R.id.btn_post);

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy đối tượng HackNasa từ danh sách hackNasaList tại vị trí position và gán vào biến hackNasa2.
                hackNasa2 = hackNasaList.get(position);

                // Kiểm tra phiên bản Android và xử lý dữ liệu theo phiên bản đó.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (hackNasa2.getHdurl() != null) {
                        // Nếu hdurl không null, chuyển đổi hdurl thành chuỗi Base64 và gán vào biến base64UrlHd.
                        base64UrlHd = convertUrlToBase64(hackNasa2.getHdurl());
                    } else {
                        // Nếu hdurl là null, gán base64UrlHd thành chuỗi rỗng.
                        base64UrlHd = "";
                    }
                    if (hackNasa2.getUrl() != null) {
                        // Nếu url không null, chuyển đổi url thành chuỗi Base64 và gán vào biến base64url.
                        base64url = convertUrlToBase64(hackNasa2.getUrl());
                    } else {
                        // Nếu url là null, gán base64url thành chuỗi rỗng.
                        base64url = "";
                    }
                }

                // Cập nhật hdurl và url của đối tượng hackNasa2 thành các chuỗi Base64 đã tính toán.
                hackNasa2.setHdurl(base64UrlHd);
                hackNasa2.setUrl(base64url);

                // Ghi thông tin của đối tượng hackNasa2 vào Log để kiểm tra nếu cần thiết.
                Log.d("sendDataToServer", hackNasa2.toString());

                // Gọi phương thức postData của APIservice để gửi dữ liệu hackNasa2 lên máy chủ thông qua API.
                Apiserver.apiService.postData(hackNasa2).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        // Hiển thị thông báo "thanh cong" nếu gửi dữ liệu thành công.
                        Toast.makeText(context, "thanh cong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Hiển thị thông báo "lỗi" và ghi thông tin lỗi vào Log nếu gặp lỗi trong quá trình gửi dữ liệu.
                        Toast.makeText(context, "lỗi", Toast.LENGTH_SHORT).show();
                        Log.d("API", t.getMessage());
                    }
                });
            }
        });



        // Hiển thị dữ liệu của mục vào các thành phần giao diện tương ứng trong itemView
        titleTextView.setText(hackNasa.getTitle());
        dateTextView.setText(hackNasa.getDate());
        explanationTextView.setText(hackNasa.getExplanation());

// Kiểm tra hdurl của đối tượng hackNasa và tải ảnh tương ứng bằng Glide
        if (hackNasa.getHdurl() != null) {
            // Nếu hdurl không null, tải ảnh từ hdurl bằng Glide và hiển thị vào imageView.

            Glide.with(context).load(hackNasa.getHdurl()).error(R.drawable.baseline_error_24).into(imageView);
        } else {

            // Nếu không thể tải được ảnh, hiển thị ảnh lỗi (baseline_error_24).
            Glide.with(context).load(hackNasa.getUrl()).error(R.drawable.baseline_error_24).into(imageView);
        }

// Trả về itemView, đã được cập nhật dữ liệu, để hiển thị trong RecyclerView
        return itemView;

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertUrlToBase64(String url) {
        if (url == null) {
            return ""; // hoặc xử lý trường hợp URL là null theo nhu cầu
        }
        byte[] byteInput = url.getBytes();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String encodedString = base64Encoder.encodeToString(byteInput);
        return encodedString;
    }

}
