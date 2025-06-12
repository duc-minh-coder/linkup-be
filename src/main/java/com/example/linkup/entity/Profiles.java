    package com.example.linkup.entity;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.*;
    import lombok.experimental.FieldDefaults;

    import java.util.Date;

    @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class Profiles {
        @Id
        @Column(name = "user_id")
        int userId;

        @Column(name = "full_name")
        @NotNull
        String fullName;

        @Column(name = "profile_picture_url")
        @Builder.Default
        String profilePictureUrl = null;

        @Column(name = "bio")
        @Builder.Default
        String bio = null;

        @Column(name = "avatar_url")
        @Builder.Default
        String avatarUrl = null;

        @Column(name = "location")
        @Builder.Default
        String location = null;

        @Column(name = "birthday")
        @Builder.Default
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date birthday = null;

        @Column(name = "updated_time")
        @Builder.Default
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date updatedTime = null;

        @OneToOne
        @JoinColumn(name = "user_id")
        @MapsId
        Users users;
    }
