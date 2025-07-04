package com.junyan.backend.file;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/files")
public class FileController {
  
  private final FileService fileService;
  private final FileRepository fileRepository;
  private final FileMapper fileMapper;

  public FileController(FileService fileService, FileRepository fileRepository, FileMapper fileMapper) {
    this.fileService = fileService;
    this.fileRepository = fileRepository;
    this.fileMapper = fileMapper;
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileDto> getFile(@PathVariable("id") Long fileId) {
    Optional<File> file = fileService.getFile(fileId);

    if (!file.isPresent()) return ResponseEntity.notFound().build();

    FileDto fileDto = fileMapper.toDto(file.get());
    return ResponseEntity.ok().body(fileDto);
  }

  @GetMapping("/by-path")
  public ResponseEntity<FileDto> getFileByPath(@RequestParam String path) {
    File file = fileService.getFileByPath(path);
    return ResponseEntity.ok().body(fileMapper.toDto(file));
  }

  @GetMapping(params = "ids")
  public ResponseEntity<List<FileDto>> getFiles(@RequestParam("ids") List<Long> fileIds) {
    List<FileDto> dtos = fileService.getFiles(fileIds)
      .stream()
      .map(opt -> opt.map(fileMapper::toDto).orElse(null))
      .toList();
    return ResponseEntity.ok().body(dtos);
  }

  @GetMapping("/parents")
  public ResponseEntity<List<Long>> getDirectoryParents(@RequestParam("id") Long directoryId) {
    return ResponseEntity.ok().body(fileService.getDirectoryParents(directoryId));
  }

  @GetMapping(params = "directory")
  public ResponseEntity<List<FileDto>> getDirectoryChildren(@RequestParam("directory") String directoryPath) {
    List<FileDto> dtos = fileService.getDirectoryChildren(directoryPath)
      .stream()
      .map(fileMapper::toDto)
      .toList();
    return ResponseEntity.ok().body(dtos);
  }

  @GetMapping("/extensions")
  public ResponseEntity<List<ExtensionCount>> getExtensionCounts(@RequestParam("root") String root) {
    return ResponseEntity.ok().body(fileRepository.getExtensionCounts(root));
  }

  @PostMapping
  public ResponseEntity<FileDto> addFile(@RequestBody FileDto fileDto) {
    if (fileRepository.existsByPath(fileDto.getPath())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(fileDto);
    }

    File file = fileMapper.toFile(fileDto);
    File created = fileService.saveFile(file);
    URI location = URI.create("/files/" + created.getId());
    return ResponseEntity.created(location).body(fileDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<FileDto> updateFile(@PathVariable Long id, @RequestBody FileDto fileDto) {
    File file = fileMapper.toFile(fileDto);
    fileService.updateFile(file);
    return ResponseEntity.ok().body(fileDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteFile(@PathVariable Long id) {
    fileService.deleteFile(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<String> deleteAll() {
    fileService.deleteAll();
    return ResponseEntity.noContent().build();
  }
}
