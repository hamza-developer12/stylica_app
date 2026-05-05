package com.example.stylica_app.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.stylica_app.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {
    private Cloudinary cloudinary;

    Map config = new HashMap();
    public CloudinaryHelper(Context context) {
        config.put("cloud_name", context.getString(R.string.cloud_name));
        config.put("api_key",context.getString(R.string.cloud_api_key));
        config.put("api_secret", context.getString(R.string.cloud_api_secret));

        cloudinary = new Cloudinary(config);
    }
    public String uploadBitmap(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();

        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                "folder", "stylica_app/images"
        ));
        return (String) uploadResult.get("secure_url");
    }
}
