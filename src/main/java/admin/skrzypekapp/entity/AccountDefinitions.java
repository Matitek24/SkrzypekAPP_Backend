package admin.skrzypekapp.entity;

import jakarta.persistence.*;

@Entity
public class AccountDefinitions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String icon;

    @Column(name = "group_type")
    private String groupType;

}
