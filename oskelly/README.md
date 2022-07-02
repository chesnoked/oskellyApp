## Порядок сборки
1. Собираем фронт(*npm run build:prod*) ->  на выходе статтика в *src/main/resources/static/* и списко собранных файлов в *src/main/resources/static/webpack_assets.json*.
2. Сборка через Maven(clean, затем compile).
Можно запускать.

## Запуск
**Рекомандуемая версия JDK - 1.8.0_152**
### Запуск на тестовом сервере:
```sh
java -jar app.jar --spring.profiles.active=dev --spring.config.location=/path/to/additional/settings
```

Приложение запускается с профилем настроек *dev*. Это значит, что приложение возьмет настройки из файла настроек `application-dev`, доступном в classpath.
Кроме того, в файле, который указан в необязательной опции `--spring.config.location`, ты можешь указать дополнительные настройки, 
которые объединятся с настройками профиля и при необходимости переопределят совпадающие. 
Например, в этом файле можно задать настройки, которые не хочется хранить в самом приложении (и системе контроля версий), 
вроде доступов к базам данных и токенов api внешних сервисов.

### Запуск на локальной машине:
```sh
java -jar app.jar --spring.profiles.active=debug --spring.config.location=/path/to/additional/settings
```

## Сборка фронта
Для сборки требуется NodeJS, NPM. 
Далее ставим webpack и подтягиваем зависимости проекта:
```sh
cd frontend
npm install --save-dev webpack
npm install
```
В **package.json** в секции "scripts" прописаны команды сборки. Базвоый вариант сборки front
```sh
npm run build:prod
```

## Компановка статических файлов
Статика, собранная WebPack подключается в бине *staticFilesConfiguration*. 
Подключение происходит по данным из файла *src/main/resources/static/webpack_assets.json*  который **не включен в контроль версий**.
Файл генерится плагином *assets-webpack-plugin*(см. "devDependencies" в package.json). Его структура:
```js
{
  "main": {
    "js": "/styles/webpack-main.js",
    "css": "/styles/main.df7a9e3ce72d77d850d4f749478b4c2e.css"
  }
}
```
Потому, перед сборкой бека надо собрать фронт, чтобы появились нужные файлы.

#Подключение статики в шаблоне
Пример
```thymeleafexpressions
<link rel="stylesheet" th:href="${@staticFilesConfiguration['main']['css']}">
```