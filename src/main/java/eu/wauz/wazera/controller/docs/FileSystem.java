package eu.wauz.wazera.controller.docs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
	
	public static List<FileSystem> findAll() {
		List<FileSystem> fileSystems = new ArrayList<>();
		for(FileStore store : FileSystems.getDefault().getFileStores()) {
			try {
				if(store.getTotalSpace() > Math.pow(1028, 3) * 16) {
					fileSystems.add(new FileSystem(store));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileSystems;
	}
	
	private String name = "Unknown";
	
	private String type = "Unknown";
	
	private String free = "Unknown";
	
	private String total = "Unknown";
	
	private int usage = 0;
	
	private FileSystem(FileStore store) throws IOException {
		name = store.name();
		type = store.type();
		long freeSpace = store.getUnallocatedSpace();
		long totalSpace = store.getTotalSpace();
		free = getByteUnit(freeSpace);
		total = getByteUnit(totalSpace);
		usage = (int) ((float) (totalSpace - freeSpace) / (float) totalSpace * 100.0);
	}
	
	private String getByteUnit(long bytes) {
		if (bytes < 1024) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(1024));
		return String.format("%.0f %sB", bytes / Math.pow(1024, exp), ("KMGTPE").charAt(exp - 1));
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getFree() {
		return free;
	}

	public String getTotal() {
		return total;
	}

	public int getUsage() {
		return usage;
	}

}
