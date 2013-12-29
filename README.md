# Проект CloudQueue 

Использует Openfire как сервер и является плагином

База Postgres 9.1

ORM Hibernate 4.1

### Для того чтобы запустить приложение
 
1. Скачать исходники Openfire 3.7.1 отсюда http://www.igniterealtime.org/downloads/index.jsp 
2. Скачать приложение CloudQueue с GitHub'a
3. Вставить все из 2 в 1 заменив если будут конфликты (src/test,src/build) 
4. New java project -> import -> existing project
5. Добавить build.xml в eclipse (находится в папке build)
6. Запустить "ant openfire"    
7. Запустить "ant plugin"   
8. Прогнать other/sql/ofproperty 
9. Зайти в target/openfire/bin и запустить "chmod +x openfire.sh" и "./openfire.sh" или openfire.bat(если Windows)
10. Консоль должна чтото вроде этого написать

    Konsol uspecho zapuchena po adressam  http://cq.kz:9090
    Bee Q plugin initialize begin
    ...
    Main components started
    Bee Q plugin initialized in: 3454 ms
11. Открыть src/test/java/kz.bee.cq/CloudPluginTest и запустить его!
12. Теперь посмотреть что есть в базу q_user и зайти под паролем "q"   

*Проект использует доп. библиотеку cloudAuth исходники которой лежат в other/src