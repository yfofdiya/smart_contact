package com.practice.helper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommonMethods {

    public static String getConvertedDate() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        String formattedDate = sdf.format(currentDate);
        return formattedDate;
    }

    public static String getImageName(MultipartFile file) {
        String[] imageNameArray = file.getOriginalFilename().split("\\.");
        String onlyImageName = imageNameArray[0];
        String extension = imageNameArray[1];
        String imageName = onlyImageName + "_" + getConvertedDate() + "." + extension;
        return imageName;
    }
}
