package com.summit.chat.Utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageUtil {

    public static int getImageWidth(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return -1;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return Thumbnails.of(inputStream).scale(1.0).asBufferedImage().getWidth();
        }
    }

    public static int getImageHeight(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            return -1;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return Thumbnails.of(inputStream).scale(1.0).asBufferedImage().getHeight();
        }
    }
}