# BabushkinPogreb
TelegramBotStorage
Скачать образ rabbitmq:
$ docker pull rabbitmq:3.11.0-management


Создать volume:
$ docker volume create rabbitmq_data


Запустить контейнер с rabbitmq:
$ docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -v rabbitmq_data:/var/lib/rabbitmq --restart=unless-stopped rabbitmq:3.11.0-management
Флаги:
--detach , -d запустит контейнер в фоновом режиме и вернет идентификатор контейнера в терминал;
--hostname адрес контейнера для подключения к нему внутри docker из других контейнеров;
--name имя контейнера;
-p порты: первый порт — тот, по которому мы будет подключаться снаружи docker, а второй — тот, который используется внутри контейнера;
-v примонтировать volume (том), т. е. внешнее хранилище данных;
--restart=unless-stopped контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);


Так путь к volume может выглядеть в Windows:
rabbitmq_data:c:\rabbitmq_data


Подключиться к контейнеру с rabbitmq:
$ docker exec -it rabbitmq /bin/bash


Внутри контейнера создать пользователя, сделать его админом и установить права:
$ rabbitmqctl add_user userok p@ssw0rd
$ rabbitmqctl set_user_tags userok administrator
$ rabbitmqctl set_permissions -p / userok ".*" ".*" ".*"


БД: pogreb
Пользователь: userok
Пароль: p@ssw0rd

Команда для разворачивания PostgreSQL в Docker:
docker run -d --hostname pogreb --name pogreb -p 5432:5432 -e POSTGRES_USER=userok -e POSTGRES_PASSWORD=p@ssw0rd -e POSTGRES_DB=pogreb -v /data:/var/lib/postgresql/data --restart=unless-stopped postgres:14.5

Так путь к volume может выглядеть в Windows:
/data:c:\postgres_data

Флаги:
--detach , -d запустит контейнер в фоновом режиме и вернет идентификатор контейнера;
--hostname адрес контейнера для подключения к нему внутри docker из других контейнеров;
--name имя контейнера;
-p порты: первый порт — тот, который мы увидим снаружи docker, а второй — тот, который внутри контейнера;
-e задает переменную окружения в контейнере;
-v примонтировать volume (том);
--restart=unless-stopped контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);
