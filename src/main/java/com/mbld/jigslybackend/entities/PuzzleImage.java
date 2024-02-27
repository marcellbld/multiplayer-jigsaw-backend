package com.mbld.jigslybackend.entities;


import lombok.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuzzleImage {
    private String imageBase64;
    private Integer width;
    private Integer height;

    public PuzzleImage(String imageBase64) throws IOException {
        this.imageBase64 = imageBase64;

        String base64Image = imageBase64.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        if(imgWidth > imgHeight) {
            this.width = 1000;
            this.height = (int)(1000 * ((float)imgHeight/imgWidth));
        } else {
            this.height = 1000;
            this.width = (int)(1000 * ((float)imgWidth/imgHeight));
        }
    }
}
