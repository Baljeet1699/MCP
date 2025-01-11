package com.MCP.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String courseId;
    private String courseName;
    private String courseDesc;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Video> videos;
}
