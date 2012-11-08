package es.chent;

import java.util.List;
import java.util.Map;

public interface PicasaIfz {

	
	 public Map<String, String> getAlbumsIdsByAlbumTitles();
	 public void createAlbum(String album_name);
	 public void addPicture(byte[] picture_data, String album_name);
	 
	 public List<String> getAlbumPhotoUrls(String albumId);
	 
	 public boolean isAttached();
	 
}
