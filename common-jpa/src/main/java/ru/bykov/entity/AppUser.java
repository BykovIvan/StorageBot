package ru.bykov.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.bykov.entity.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;
    @CreationTimestamp                       //создаст дату при сохранении модели в бд
    private LocalDateTime firstLoginDate;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState state;
}
