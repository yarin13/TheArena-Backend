package arena.bll;

import arena.dal.DBManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ImagesManager {

    public static void getAllImages(HttpServletRequest request, HttpServletResponse response ,String email,OutputStream os) throws IOException {
        if (UsersManager.emailValidation(email)){
            Users user = UsersManager.returnUserId(email);
            assert user != null;

            AtomicInteger totalPic = new AtomicInteger();
            DBManager.runSelect(String.format("select COUNT(photo) as 'total' from usersPhotos where userId = %d;",user.getId()),resultSet -> {
                try {
                    totalPic.set(resultSet.getInt("total"));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            Blob[] photos = new Blob[totalPic.get()];
            int photosBlobIndex = 0;
            ArrayList<Integer> photosId = new ArrayList<>();

            if (totalPic.get() > 0 ) {
                DBManager.runSelect(String.format("select id from usersPhotos where userId = %d;", user.getId()), resultSet -> {
                    try {
                        photosId.add(resultSet.getInt("id"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }
            ArrayList<byte[]> bytes = new ArrayList<>();
            AtomicReference<InputStream> inputStream = new AtomicReference<>();
            for (int i = 0; i < totalPic.get(); i++) {
                int finalPhotosBlobIndex = photosBlobIndex;

                DBManager.runSelect(String.format("select photo from usersPhotos where id = %d", photosId.get(i)), resultSet -> {
                    try {
                        photos[finalPhotosBlobIndex] = resultSet.getBlob("photo");
                        bytes.add(photos[finalPhotosBlobIndex].getBytes(1,(int) photos[finalPhotosBlobIndex].length()));
                        inputStream.set(resultSet.getBinaryStream("photo"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
                photosBlobIndex++;
            }

            byte[] buffer = new byte[2048];
            int bytesRead;

            for (int i =0 ; i < totalPic.get();i++) {
                while ((bytesRead = inputStream.get().read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            os.close();
        }

    }
}
