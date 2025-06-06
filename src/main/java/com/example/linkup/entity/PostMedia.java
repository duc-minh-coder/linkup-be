package com.example.linkup.entity;

import com.example.linkup.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "media_type")
    MediaType mediaType;

    @Column(name = "url")
    String url;

    @Column(name = "order_index")
    short orderIndex;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    Posts post;
}
