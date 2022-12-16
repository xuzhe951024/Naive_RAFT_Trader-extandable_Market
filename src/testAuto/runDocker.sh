# docker pull alpine/socat
# docker run \
#     --publish 4321:1234 \
#     --link example-container:target \
#     alpine/socat \
#     tcp-listen:1234,fork,reuseaddr tcp-connect:target:1234

 java -cp app.jar -Dloader.main=com.zhexu.cs677_lab3.utils.ProfilesGenerator org.springframework.boot.loader.PropertiesLauncher config.yml
 echo ./cs677.lab*.peer*.com | xargs -n 1 cp app.jar
 echo ./cs677.lab*.peer*.com | xargs -n 1 cp wait-for.sh
 docker-compose up -d