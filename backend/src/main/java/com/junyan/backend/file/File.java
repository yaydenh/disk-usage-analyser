package com.junyan.backend.file;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "file_entity", indexes = {
  @Index(name = "path_index", columnList = "path"),
  @Index(name = "depth_index", columnList = "depth")
})
public class File {
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;

  @Column(name = "path")
  private String path;

  @Column(name = "size")
  private Long size;

  @Column(name = "numItems")
  private Integer numItems;

  @Column(name = "numFiles")
  private Integer numFiles;

  @Column(name = "extension")
  private String extension;

  @Column(name = "created")
  private LocalDateTime created;

  @Column(name = "last_modified")
  private LocalDateTime lastModified;

  @Column(name = "is_directory")
  private boolean isDirectory;

  @Column(name = "depth")
  private int depth;

  public File() {
  }

  public File(String path,
              Long size,
              Integer numItems,
              Integer numFiles,
              String extension,
              LocalDateTime created,
              LocalDateTime lastModified,
              boolean
              isDirectory,
              int depth) {
    this.path = path;
    this.size = size;
    this.numItems = numItems;
    this.numFiles = numFiles;
    this.extension = extension;
    this.created = created;
    this.lastModified = lastModified;
    this.isDirectory = isDirectory;
    this.depth = depth;
  }

  public File(Long id,
              String path,
              Long size,
              Integer numItems,
              Integer numFiles,
              String extension,
              LocalDateTime created,
              LocalDateTime lastModified,
              boolean isDirectory,
              int depth) {
    this.id = id;
    this.path = path;
    this.size = size;
    this.numItems = numItems;
    this.numFiles = numFiles;
    this.extension = extension;
    this.created = created;
    this.lastModified = lastModified;
    this.isDirectory = isDirectory;
    this.depth = depth;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Integer getNumItems() {
    return numItems;
  }

  public void setNumItems(Integer numItems) {
    this.numItems = numItems;
  }

  public Integer getNumFiles() {
    return numFiles;
  }

  public void setNumFiles(Integer numFiles) {
    this.numFiles = numFiles;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(LocalDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public void setDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }

  public int getDepth() {
    return this.depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }
}
