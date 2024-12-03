# P6-PayMyBuddy

    MPD

    USERS : 
    id integer (PK)
    username varchar(55)
    email varchar(55)
    password varchar(255)

    TRANSACTIONS :
    id integer (PK)
    description varchar(255)
    amount double
    sender_id integer (FK)
    receiver_id integer (FK)

    users_connections :
    (id_first_user integer, id_second_user integer) (PK)
    id_first_user integer (FK)
    id_second_user integer (FK)

![MPD.png](MPD.png)