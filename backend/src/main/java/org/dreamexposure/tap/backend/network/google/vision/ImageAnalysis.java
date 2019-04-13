package org.dreamexposure.tap.backend.network.google.vision;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.dreamexposure.tap.backend.network.database.PostDataHandler;
import org.dreamexposure.tap.core.conf.SiteSettings;
import org.dreamexposure.tap.core.enums.post.SafeSearchRating;
import org.dreamexposure.tap.core.objects.post.ImagePost;
import org.dreamexposure.tap.core.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageAnalysis {
    private static ImageAnalysis instance;

    private ImageAnnotatorSettings settings;

    private ImageAnalysis() {
    }

    public static ImageAnalysis get() {
        if (instance == null)
            instance = new ImageAnalysis();

        return instance;
    }

    public void init() {
        try {
            File creds = new File(SiteSettings.CREDENTIAL_FOLDER.get() + "/cloud-vision-creds.json");
            InputStream stream = new FileInputStream(creds);

            Credentials c = GoogleCredentials.fromStream(stream);

            settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(c)).build();

            stream.close();
        } catch (Exception e) {
            Logger.getLogger().exception("Failed to init Vision Settings", e, true, this.getClass());
        }
    }

    public void handleImageScan(ImagePost post) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<AnnotateImageRequest> requests = new ArrayList<>();
                    //Read image from disk...
                    byte[] bytes = Files.readAllBytes(Paths.get(post.getImage().getPath()));
                    ByteString imgBytes = ByteString.copyFrom(bytes);

                    //Build image request
                    Image img = Image.newBuilder()
                            .setContent(imgBytes)
                            .build();
                    Feature feat = Feature.newBuilder()
                            .setType(Feature.Type.SAFE_SEARCH_DETECTION)
                            .build();
                    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                            .addFeatures(feat)
                            .setImage(img)
                            .build();
                    requests.add(request);

                    //Make request...
                    ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);
                    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                    List<AnnotateImageResponse> responses = response.getResponsesList();

                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            Logger.getLogger().exception("Safe search error: " + res.getError().getMessage(), null, true, this.getClass());

                            System.out.println("Safe search error: " + res.getError());
                        } else {
                            SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();
                            SafeSearchRating adult = SafeSearchRating.fromString(annotation.getAdult().name());
                            SafeSearchRating medical = SafeSearchRating.fromString(annotation.getMedical().name());
                            SafeSearchRating spoof = SafeSearchRating.fromString(annotation.getSpoof().name());
                            SafeSearchRating violence = SafeSearchRating.fromString(annotation.getViolence().name());
                            SafeSearchRating racy = SafeSearchRating.fromString(annotation.getRacy().name());

                            //set post to NSFW is needed
                            if (adult.getScore() > 0.7)
                                post.setNsfw(true);
                            if (medical.getScore() > 0.7)
                                post.setNsfw(true);
                            if (spoof.getScore() > 0.7)
                                post.setNsfw(true);
                            if (violence.getScore() > 0.7)
                                post.setNsfw(true);
                            if (racy.getScore() > 0.7)
                                post.setNsfw(true);

                            //Update it in database
                            PostDataHandler.get().addPost(post);

                            Logger.getLogger().debug("Image scan with Vision complete for image hash: " + post.getImage().getHash(), false);
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger().exception("Failed to handle vision analysis", e, true, this.getClass());
                }
            }
        });

        thread.start();
    }
}
