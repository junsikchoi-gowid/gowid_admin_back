package com.nomadconnection.dapp.resx.service;

import com.nomadconnection.dapp.resx.exception.FailedToCreateDirectoriesException;
import com.nomadconnection.dapp.resx.exception.FailedToSaveException;
import com.nomadconnection.dapp.resx.exception.ResxAlreadyExistException;
import com.nomadconnection.dapp.resx.exception.UnacceptableResxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Comparator;

@Slf4j
@Service
@SuppressWarnings("unused")
public class ResourceService {

//	private final Path root;
//	private final ResxConfig config;

	private void mkdirs(Path path) {
		if (!Files.exists(path.getParent())) {
			try {
				Files.createDirectories(path.getParent());
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error("([ mkdirs ]) FAILED TO CREATE DIRECTORIES, $path='{}', $exception='{} => {}'", path, e.getClass().getSimpleName(), e.getMessage(), e);
				}
				throw FailedToCreateDirectoriesException.builder()
						.cause(e)
						.message(e.getMessage())
						.path(path)
						.build();
			}
		}
	}

	/**
	 * 디렉토리 삭제
	 *
	 * @param directory 삭제할 디렉토리의 상대경로
	 */
	public void rmdir(String directory) throws IOException {

		Path path = Paths.get(directory).toAbsolutePath().normalize();

		if (Files.exists(path)) {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(
					file -> {
						if (file.delete()) {
							if (log.isInfoEnabled()) {
								log.info("([ rmdir ]) REMOVED, $path='{}'", file.getAbsolutePath());
							}
						} else {
							if (log.isWarnEnabled()) {
								log.warn("([ rmdir ]) FAILED TO REMOVE, $path='{}'", file.getAbsolutePath());
							}
						}
					});
		}
	}


	public void save(MultipartFile resx, Path path, boolean replace) {
		if (resx == null || resx.isEmpty()) {
			throw UnacceptableResxException.builder()
					.message("EMPTY RESX")
					.resx(resx)
					.build();
		}
		if (resx.getOriginalFilename() == null || resx.getOriginalFilename().contains("..")) {
			throw UnacceptableResxException.builder()
					.message("UNACCEPTABLE RESX")
					.resx(resx)
					.name(resx.getOriginalFilename())
					.build();
		}
		if (!path.isAbsolute()) {
			path = path.toAbsolutePath();
		}
		path = path.normalize();
		{
			if (!replace && Files.exists(path)) {
				throw ResxAlreadyExistException.builder()
						.path(path)
						.build();
			}
			mkdirs(path); // FailedToCreateDirectoriesException
		}
		try {
			Files.copy(resx.getInputStream(), path, replace ? StandardCopyOption.REPLACE_EXISTING : null);
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("([ save ]) FAILED TO COPY RESX, $resx='{}', $path='{}', $replace='{}', $exception='{} => {}'",
						resx.getOriginalFilename(), path, replace, e.getClass().getSimpleName(), e.getMessage(), e);
			}
			throw FailedToSaveException.builder()
					.cause(e)
					.path(path)
					.build();
		}
	}

	public Resource resource(Path path) {
		try {
			Resource resx = new UrlResource(path.toAbsolutePath().normalize().toUri());
			{
				if (resx.exists()) {
					return resx;
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("([ retrieve ]) RESOURCE NOT FOUND, $path='{}', $resx='{}'", path, resx);
			}
		} catch (MalformedURLException e) {
			if (log.isErrorEnabled()) {
				log.error("([ retrieve ]) MALFORMED URL, $path='{}'", path);
			}
		}
		return null;
	}

	public boolean remove(Path path, boolean suppress) throws IOException {
		try {
			if (Files.deleteIfExists(path)) {
				if (log.isInfoEnabled()) {
					log.info("([ remove ]) DELETED, $path='{}'", path);
				}
				return true;
			}
		} catch (InvalidPathException e) {
			if (log.isWarnEnabled()) {
				log.warn("([ remove ]) SKIPPED(INVALID PATH), $path='{}', $exception='{} => {}'", path, e.getClass().getSimpleName(), e.getMessage(), e);
			}
			if (!suppress) {
				throw e;
			}
			return true;
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("([ remove ]) ERROR, $path='{}', $exception='{} => {}'", path, e.getClass().getSimpleName(), e.getMessage(), e);
			}
			if (!suppress) {
				throw e;
			}
		}
		return false;
	}

//	public ResxService(ResxConfig config) {
//		this.config = config;
//		if (log.isInfoEnabled()) {
//			log.info("([ ResxService ]) $config='{}'", config);
//		}
//		root = config.getAbsoluteResxRootPath();
//		if (!Files.exists(root)) {
//			try {
//				Files.createDirectories(root);
//			} catch (IOException e) {
//				if (log.isErrorEnabled()) {
//					log.error("[( ResxService )] $error='failed to create resx directories, {}', $exception='{} => {}'",
//							root,
//							e.getClass().getSimpleName(),
//							e.getMessage(),
//							e);
//				}
//				throw FailedToCreateDirectoriesException.builder()
//						.message("failed to create resx directories, `" + root + "`")
//						.path(root)
//						.build();
//			}
//		}
//	}

//	public String getBaseUri(Long id) {
//		return String.format("%s/%d/", config.getResxUriPrefix(), id);
//	}
//
//	public String getUri(Long id, String filename) {
//		return getBaseUri(id) + filename;
//	}

//	private Path resolve(Long id, String filename) throws InvalidPathException {
//		return root.resolve(
//				String.format("%d%s%s", id, File.separator, filename)
//		);
//	}

//	/**
//	 * save resource
//	 *
//	 * @param id identifier(parent directory name of resource)
//	 * @param resource resource
//	 * @return stored resource dto
//	 */
//	@Deprecated
//	public ResxDto save(Long id, MultipartFile resource) {
//
//		ResxDto result = ResxDto.builder()
//				.id(id)
//				.status(ResxDto.Status.ERROR)
//				.build();
//
//		if (id == null || id < 0) {
//			if (log.isDebugEnabled()) {
//				log.debug("([ save ]) $error='{}'", ResxDto.Status.ERROR_INVALID_ID_PARAMETER);
//			}
//			return result.status(ResxDto.Status.ERROR_INVALID_ID_PARAMETER);
//		}
//
//		if (resource == null || resource.getOriginalFilename() == null || resource.getOriginalFilename().contains("..")) {
//			if (log.isDebugEnabled()) {
//				log.debug("([ save ]) $error='{}'", ResxDto.Status.ERROR_INVALID_RESOURCE_PARAMETER);
//			}
//			return result.status(ResxDto.Status.ERROR_INVALID_RESOURCE_PARAMETER);
//		}
//
//		result.originalFilename(resource.getOriginalFilename());
//		result.filename(UUID.randomUUID().toString() + result.originalFilename().substring(result.originalFilename().lastIndexOf('.')));
//
//		try {
//			result.path(resolve(id, result.filename()));
//			Files.createDirectories(result.path().getParent());
//			Files.copy(resource.getInputStream(), result.path(), StandardCopyOption.REPLACE_EXISTING);
//			result.size(Files.size(result.path()));
//		} catch (InvalidPathException e) {
//			if (log.isErrorEnabled()) {
//				log.error("([ save ]) $error='{}', $id='{}', $filename='{}', $exception='{} => {}'",
//						ResxDto.Status.ERROR_INVALID_PATH_EXCEPTION, id, result.filename(),
//						e.getClass().getSimpleName(),
//						e.getMessage(),
//						e);
//			}
//			return result.status(ResxDto.Status.ERROR_INVALID_PATH_EXCEPTION);
//		} catch (IOException e) {
//			if (log.isErrorEnabled()) {
//				log.error("([ save ]) $error='{}', $path='{}', $exception='{} => {}'",
//						ResxDto.Status.ERROR_IO_EXCEPTION, result.path(),
//						e.getClass().getSimpleName(), e.getMessage(), e);
//			}
//			return result.status(ResxDto.Status.ERROR_IO_EXCEPTION);
//		}
//
//		return result.status(ResxDto.Status.OK);
//	}

//	/**
//	 * find resource
//	 *
//	 * @param id identifier(parent directory name of resource)
//	 * @param filename filename(stored)
//	 * @return resource
//	 */
//	@Deprecated
//	public Resource resource(Long id, String filename) {
//
//		Resource resource;
//
//		if (id != null && !StringUtils.isEmpty(filename)) {
//			try {
//				resource = new UrlResource(resolve(id, filename).normalize().toUri());
//				if (resource.exists()) {
//					return resource;
//				} else {
//					if (log.isErrorEnabled()) {
//						log.error("([ resource ]) $error='RESOURCE NOT FOUND', $id='{}', $filename='{}'", id, filename);
//					}
//				}
//			} catch (MalformedURLException e) {
//				if (log.isErrorEnabled()) {
//					log.error("([ resource ]) $error='MALFORMED URL', $id='{}', $filename='{}'", id, filename, e);
//				}
//			}
//		} else {
//			if (log.isDebugEnabled()) {
//				log.debug("([ resource ]) $error='INVALID PARAMETER', $id='{}', $filename='{}'", id, filename);
//			}
//		}
//		return null;
//	}

//	/**
//	 * remove resource
//	 *
//	 * @param id identifier(parent directory name of resource)
//	 * @param filename filename(stored)
//	 * @return true if succeeded, otherwise false
//	 */
//	@Deprecated
//	public boolean remove(Long id, String filename) {
//
//		Path path;
//
//		if (id != null && !StringUtils.isEmpty(filename)) {
//			try {
//				path = resolve(id, filename);
//			} catch (InvalidPathException e) {
//				if (log.isWarnEnabled()) {
//					log.warn("([ remove ]) $path(invalid)='...', $id='{}', $filename='{}', $exception='{} => {}'", id, filename, e.getClass().getSimpleName(), e.getMessage(), e);
//				}
//				return true;
//			}
//			try {
//				if (Files.deleteIfExists(path)) {
//					if (log.isInfoEnabled()) {
//						log.info("([ remove ]) $path(removed)='{}', $id='{}', $filename='{}'", path, id, filename);
//					}
//				}
//				return true;
//			} catch (IOException e) {
//				if (log.isErrorEnabled()) {
//					log.error("([ remove ]) $path(error)='{}', $id='{}', $filename='{}', $exception='{} => {}'", path, id, filename, e.getClass().getSimpleName(), e.getMessage(), e);
//				}
//			}
//		}
//		return false;
//	}

//	/**
//	 * 디렉토리 삭제
//	 *
//	 * @param directoryPath 삭제할 디렉토리의 상대경로
//	 */
//	public void rmdir(String directoryPath) throws InvalidPathException, IOException {
//
//		Path path = root.resolve(directoryPath);
//
//		if (Files.exists(path)) {
//			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(
//					file -> {
//						try {
//							if (file.delete()) {
//								if (log.isInfoEnabled()) {
//									log.info("([ rmdir ]) REMOVED, $path='{}'", file.getAbsolutePath());
//								}
//							} else {
//								if (log.isWarnEnabled()) {
//									log.warn("([ rmdir ]) FAILED TO REMOVE, $path='{}'", file.getAbsolutePath());
//								}
//							}
//						} catch (SecurityException e) {
//							if (log.isErrorEnabled()) {
//								log.error("([ rmdir ]) ACCESS DENIED, $path='{}', $exception='{} => {}'", file.getAbsolutePath(), e.getClass().getSimpleName(), e.getMessage(), e);
//							}
//							throw e;
//						}
//					});
//		}
//	}

//	/**
//	 * remove resource directory
//	 *
//	 * @param id identifier(parent directory name of resource)
//	 * @return true if succeeded, otherwise false
//	 */
//	@Deprecated
//	public boolean removeResxDirectory(Long id) {
//
//		Path path;
//
//		try {
//			path = root.resolve(Long.toString(id));
//			if (!Files.exists(path)) {
//				if (log.isDebugEnabled()) {
//					log.debug("([ removeResxDirectory ]) $path(NOT_FOUND)='{}', $id='{}'", path, id);
//				}
//				return true;
//			}
//		} catch (InvalidPathException e) {
//			if (log.isWarnEnabled()) {
//				log.warn("([ removeResxDirectory ]) $path(INVALID)='...', $id='{}', $exception='{} => {}'", id, e.getClass().getSimpleName(), e.getMessage(), e);
//			}
//			return true;
//		}
//		try {
//			Files.walk(path)
//					.sorted(Comparator.reverseOrder())
//					.map(Path::toFile)
//					.forEach(file -> {
//						if (file.delete()) {
//							if (log.isInfoEnabled()) {
//								log.info("([ removeResxDirectory ]) $file(REMOVED)='{}', $id='{}', $path='{}'", file.getAbsolutePath(), id, path);
//							}
//						} else {
//							if (log.isInfoEnabled()) {
//								log.info("([ removeResxDirectory ]) $file(FAILED_TO_REMOVE)='{}', $id='{}', $path='{}'", file.getAbsolutePath(), id, path);
//							}
//						}
//					});
//			return true;
//		} catch (IOException e) {
//			if (log.isErrorEnabled()) {
//				log.error("([ removeResxDirectory ]) $error(failed_to_delete_resx_dir)='{}', $id='{}', $exception='{} => {}'", path, id, e.getClass().getSimpleName(), e.getMessage(), e);
//			}
//		}
//		return false;
//	}
}
