package su.reddot.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class FileUtils {

	private FileUtils() {
	}

	/**
	 * Сохраняяем файл в директории с внешними файлами приложения
	 *
	 * @param directory путь к директории, в которой будет находиться конечный файл (включая саму директорию)
	 *                  относительно директории с внешними файлами приложения
	 * @param filename  имя, под которым следует сохранить файл
	 * @param file      сам файл
	 * @return сгенерированный класс File, ведущий на сохраненый файл
	 */
	public static File saveMultipartFile(String directory, String filename, MultipartFile file) {
		File directoryPathFile = new File(directory);
		if (!directoryPathFile.exists()) {
			directoryPathFile.mkdirs();
		}
		if (!Objects.equals(directory.substring(directory.length() - 1), "/")) {
			directory += "/";
		}

		String path = directory + filename;
		File dest = new File(path);
		try {
			file.transferTo(dest);
			return dest;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Сохраняяем файл в директории с внешними файлами приложения
	 *
	 * @param directory путь к директории, в которой будет находиться конечный файл (включая саму директорию)
	 *                  относительно директории с внешними файлами приложения
	 * @param file      сам файл
	 * @return сгенерированный класс File, ведущий на сохраненый файл
	 */
	public static File saveMultipartFileWithGeneratedName(String directory, MultipartFile file) {
		String fileName = UUID.randomUUID().toString();
		return saveMultipartFile(directory, fileName, file);
	}

	/**
	 * Удаление файла из папки с файлами
	 *
	 * @param path путь к файлу относительно директории с внешними файлами приложения
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}
}
