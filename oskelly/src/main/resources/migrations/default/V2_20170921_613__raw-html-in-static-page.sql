ALTER TABLE static_page
    ADD COLUMN is_raw_html boolean;

UPDATE static_page
SET is_raw_html = FALSE;

INSERT INTO static_page(is_raw_html, page_group, url, name, status, page_style, content)
VALUES (TRUE, 'INFO', '/welcome', 'landing-welcome', 'PUBLISHED', 'welcomePage',
'<div class="welcomePage-container row">
  <aside class="welcomePage-nav small-12">
    <ul>
      <li><a href="/info/welcome" class="welcomePage-navLink welcomePage-navLink_active"><span class="text-uppercase">Oskelly</span></a></li>
      <li><a href="/info/buy" class="welcomePage-navLink">Покупайте</a></li>
      <li><a href="/info/sell" class="welcomePage-navLink">Продавайте</a></li>
    </ul>
  </aside>
  <div class="column">
    <h1 class="welcomePage-title">Welcome</h1>
    <h2 class="welcomePage-subtitle">Покупайте. Продавайте. Делитесь!</h2>
    <hr class="welcomePage-titleDecoration">
    <div class="welcomePage-slide">
      <h4 class="welcomePage-slideTitle">01. Покупайте</h4>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/tag.svg"></div>
            <p>Выбирайте товары на&nbsp;сайте  или в&nbsp;мобильном приложении и&nbsp;оплачивайте&nbsp;их банковской картой.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/seal-of-approval.svg"></div>
            <p>Сразу после оплаты товар будет отправлен нам продавцом для&nbsp;проверки оригинальности и&nbsp;заявленных характеристик товара.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/delivery.svg"></div>
            <p>Мы&nbsp;убедимся в&nbsp;качестве и&nbsp;подлинности вещи и&nbsp;вышлем её&nbsp;вам курьерской доставкой.</p>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="welcomePage-slideAction column"><a href="/info/buy" class="welcomePage-slideActionButton">Подробнее</a></div>
      </div>
    </div>
    <div class="welcomePage-slide">
      <h4 class="welcomePage-slideTitle">02. Продавайте</h4>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/dress-plus.svg"></div>
            <p>Добавляйте товары на продажу в два клика через сайт или мобильное приложение.  Все остальное сделаем мы.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/seal-of-approval.svg"></div>
            <p>Покупатель найден! Подтвердите сделку в личном кабинете. После подтверждения  наш курьер заберет вещь и привезет на экспертизу в наш офис. Вам ничего не нужно платить.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/money-shield.svg"></div>
            <p>Проверено! Сразу после экспертизы подлинности и проверки заявленных характеристик товар отправится к покупателю, а вы получите оплату на свой банковский счет.</p>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="welcomePage-slideAction column"><a href="/info/sell" class="welcomePage-slideActionButton">Подробнее</a></div>
      </div>
    </div>
    <div class="welcomePage-slide welcomePage-slide-description">
      <div class="row align-middle large-unstack">
        <div class="welcomePage-slideContent small-12 large-7 columns">
          <h4 class="welcomePage-slideTitle">Покупка</h4>
          <p>Откройте для себя мир брендовых вещей превосходного качества по&nbsp;ценам ниже розничных. Огромный выбор внимательно отобранных нашей командой товаров, ежедневные поступления и&nbsp;многообразие брендов на&nbsp;<span class="text-uppercase">Oskelly</span> – ваше пространство для&nbsp;вдохновения!</p><br>
          <p><a href="/catalog/just-in" class="welcomePage-slideActionButton">Подробнее</a></p>
        </div>
        <div class="welcomePage-slideImage small-12 large-5 columns"><img height="312px" src="/images/pictures/woman-in-black.png" alt="девушка в чёрном платье"></div>
      </div>
    </div>
    <div class="welcomePage-slide welcomePage-slide-description">
      <div class="row align-middle large-unstack">
        <div class="welcomePage-slideImage small-12 large-5 columns"><img height="312px" src="/images/pictures/shirt-tag-mockup.png" alt="Бирка"></div>
        <div class="welcomePage-slideContent small-12 large-7 columns">
          <h4 class="welcomePage-slideTitle">Качество и подлинность гарантированы</h4>
          <p>Каждый товар, размещенный на сайте, тщательно проверяется нашими экспертами. После проверки товар получает фирменную пломбу OSKELLY, которая подтверждает качество и оригинальность его происхождения.</p><br>
          <p><a href="/info/garantiya-podlinnosti" class="welcomePage-slideActionButton">Подробнее</a></p>
        </div>
      </div>
    </div>
    <div class="welcomePage-slide welcomePage-slide-description">
      <div class="row align-middle large-unstack">
        <div class="welcomePage-slideContent small-12 large-7 columns">
          <h4 class="welcomePage-slideTitle">Продажа</h4>
          <p>OSKELLY – это множество проверенных продавцов со всей России и тысячи товаров именитых брендов. Обновлять гардероб и зарабатывать на продаже вещей еще никогда не было так просто!</p><br>
          <p><a href="/publication" class="welcomePage-slideActionButton">Подробнее</a></p>
        </div>
        <div class="welcomePage-slideImage small-12 large-5 columns"><img height="312px" src="/images/pictures/case.png" alt="шкаф"></div>
      </div>
    </div>
    <p class="welcomePage-helpText">Остались вопросы? Обратитесь в&nbsp;<a href="/info/kontakty">службу поддержки</a></p>
    <hr>
    <aside class="welcomePage-nav">
      <ul>
        <li><a href="/info/welcome" class="welcomePage-navLink welcomePage-navLink_active"><span class="text-uppercase">Oskelly</span></a></li>
        <li><a href="/info/buy" class="welcomePage-navLink">Покупайте</a></li>
        <li><a href="/info/sell" class="welcomePage-navLink">Продавайте</a></li>
      </ul>
    </aside>
  </div>
</div>'),
(TRUE, 'INFO', '/buy', 'landing-buy', 'PUBLISHED', 'buyPage',
'<div class="buyPage-container row">
  <aside class="buyPage-nav small-12">
    <ul>
      <li><a href="/info/welcome" class="buyPage-navLink"><span class="text-uppercase">Oskelly</span></a></li>
      <li><a href="/info/buy" class="buyPage-navLink buyPage-navLink_active">Покупайте</a></li>
      <li><a href="/info/sell" class="buyPage-navLink">Продавайте</a></li>
    </ul>
  </aside>
  <div class="column">
    <h1 class="buyPage-title">Покупайте на OSKELLY</h1>
    <h2 class="buyPage-subtitle">Откройте для себя большой выбор роскошных вещей высокого качества по привлекательным ценам! Ежедневно наш интернет-магазин пополняется новыми товарами самых известных мировых брендов – Chanel, Louis Vuitton, Isabel Marant, Celine, Gucci, Burberry, Versace, Prada и других.</h2>
    <hr class="buyPage-titleDecoration">
    <div class="buyPage-slide">
      <h4 class="buyPage-slideTitle">Как начать покупать на OSKELLY?</h4>
      <div class="column large-unstack">
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/buypage-1-ico-slide-1.svg"></div>
          <p>Перед публикацией все товары проходят модерацию: мы убеждаемся в соответствии описания реальному состоянию вещи и в правильности выставленной цены. В случае необходимости OSKELLY может уменьшить цену.</p>
        </div>
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/buypage-1-ico-slide-2.svg"></div>
          <p>При покупке сумма, равная стоимости товара, замораживается на карте покупателя до подтверждения сделки с продавцом. Деньги списываются с карты, когда продавец подтверждает сделку.</p>
        </div>
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/buypage-1-ico-slide-3.svg"></div>
          <p>После оплаты наши курьеры доставят товар в офис OSKELLY для проведения экспертизы качества и подлинности товара.</p>
        </div>
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/buypage-1-ico-slide-4.svg"></div>
          <p>Если товар в точности соответствует заявленному описанию и характеристикам наши курьеры доставят его вам в заранее согласованное время.</p>
        </div>
      </div>
    </div>
    <div class="buyPage-slide welcomePage-slide-description">
      <div class="row align-middle large-unstack">
        <div class="buyPage-slideImage small-12 large-5 columns"><img height="312px" src="/images/pictures/buypage-1.png" alt="сумка на oskelly"></div>
        <div class="buyPage-slideContent small-12 large-7 columns">
          <h4 class="buyPage-slideTitle">Гарантия подлинности</h4>
          <p>Оригинальное происхождение и качество товара гарантированы: перед отправкой покупателю все товары проверяются нашими экспертами.</p>
        </div>
      </div>
    </div>
    <div class="welcomePage-slide">
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/buypage-2-ico-slide-1.svg"></div>
            <p><strong>Борьба с контрафактом</strong></p>
            <p>Команда опытных экспертов OSKELLY призвана выявлять контрафактную продукцию и поддерживать высокие стандарты качества</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/buypage-2-ico-slide-2.svg"></div>
            <p><strong>Описание товара</strong></p>
            <p>При проверке товара мы убеждаемся в том, что бренд, цвет и размер соответствуют описанию продавца</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/buypage-2-ico-slide-3.svg"></div>
            <p><strong>Состояние товара</strong></p>
            <p>В случае, если будут найдены расхождения между реальным состоянием и описанием товара, мы предложим вам новую цену. Вы так же можете отменить заказ.</p>
          </div>
        </div>
      </div>
    </div>
    <div class="buyPage-slide">
      <h4 class="buyPage-slideTitle">Наши эксклюзивные услуги</h4>
      <p class="medium-text-center">Оригинальное происхождение и качество товара гарантированы: перед отправкой покупателю все товары проверяются нашими экспертами.</p>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-1.svg"></div>
            <p><strong>Приложение OSKELLY</strong></p>
            <p>Где бы вы ни находились – бренды всегда с вами! Совершайте покупки в мобильном приложении OSKELLY. Доступно в AppStore и Google Play.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-2.svg"></div>
            <p><strong>Сообщить о наличии</strong></p>
            <p>Не упустите свою вещь: создайте оповещение для интересующего вас товара, и вы получите уведомление сразу, как только он станет доступен на сайте.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-3.svg"></div>
            <p><strong>Сделайте предложение</strong></p>
            <p>Вы можете предложить свою цену продавцу. Покупайте любимые бренды по самым приятным ценам.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-4.svg"></div>
            <p><strong>VIP-сервис</strong></p>
            <p>Премиум-клиенты могут просматривать вещи до того, как они поступят в продажу, а также зарезервировать понравившиеся товары и получить бесплатную доставку для 12 заказов в год.</p>
          </div>
        </div>
      </div>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-5.svg"></div>
            <p><strong>Баллы и рейтинги</strong></p>
            <p>Зарабатывайте баллы на покупках и продажах и оплачивайте ими новые приобретения на OSKELLY.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-6.svg"></div>
            <p><strong>Сделать подарок</strong></p>
            <p>Сделать подарок своим близким – это легко.<a class="buyPage-smallLink" href="#"> Узнайте, как.</a></p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-7.svg"></div>
            <p><strong>Следить за понижением цены</strong></p>
            <p>Отслеживайте изменения стоимости товаров в личном кабинете.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-8.svg"></div>
            <p><strong>Wish-list</strong></p>
            <p>Сохраняйте понравившиеся товары в Wish-List. Ваши близки смогут узнать, какой подарок обрадует Вас!</p>
          </div>
        </div>
      </div>
    </div>
    <p class="buyPage-helpText">Остались вопросы? Обратитесь в&nbsp;<a href="/info/kontakty">службу поддержки</a></p>
    <hr>
    <aside class="buyPage-nav">
      <ul>
        <li><a href="/info/welcome" class="buyPage-navLink"><span class="text-uppercase">Oskelly</span></a></li>
        <li><a href="/info/buy" class="buyPage-navLink buyPage-navLink_active">Покупайте</a></li>
        <li><a href="/info/sell" class="buyPage-navLink">Продавайте</a></li>
      </ul>
    </aside>
  </div>
</div>'),
(TRUE, 'INFO', '/sell', 'landing-sell', 'PUBLISHED', 'buyPage',
'
<div class="buyPage-container row">
  <aside class="buyPage-nav small-12">
    <ul>
      <li><a href="/info/welcome" class="buyPage-navLink"><span class="text-uppercase">Oskelly</span></a></li>
      <li><a href="/info/buy" class="buyPage-navLink">Покупайте</a></li>
      <li><a href="/info/sell" class="buyPage-navLink buyPage-navLink_active">Продавайте</a></li>
    </ul>
  </aside>
  <div class="column">
    <h1 class="buyPage-title">Продавайте на OSKELLY</h1>
    <h2 class="buyPage-subtitle">Мы принимаем тысячи известных мировых брендов от Sandro, Maje и Acne Studios до Chanel, Céline и Prada. С полным списком брендов вы можете ознакомиться здесь. Обновлять гардероб и зарабатывать на продаже вещей еще никогда не было так просто.</h2>
    <div class="row">
      <div class="buyPage-slideAction column"><a href="/publication" class="buyPage-slideActionButton">Начать продавать</a></div>
    </div>
    <hr class="buyPage-titleDecoration">
    <div class="buyPage-slide">
      <h4 class="buyPage-slideTitle">Как начать продавать на OSKELLY?</h4>
      <div class="column large-unstack">
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/sell-s1-1.svg"></div>
          <p>Добавляйте товары на продажу в два клика через сайт или мобильное приложение. Все остальное сделаем мы.</p>
        </div>
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/sell-s1-2.svg"></div>
          <p>Покупатель найден! Подтвердите сделку в личном кабинете.  После подтверждения наш курьер заберет вещь и привезет на экспертизу в наш офис. Вам ничего не нужно платить!</p>
        </div>
        <div class="buyPage-slideFeature row">
          <div class="buyPage-slideFeature-icon"><img src="/images/icons/sell-s1-3.svg"></div>
          <p>Проверено! Сразу после экспертизы подлинности и проверки заявленных характеристик товар отправится к покупателю, а вы получите оплату на свой банковский счет.</p>
        </div>
      </div>
    </div>
    <div class="buyPage-slide">
      <h4 class="buyPage-slideTitle">Консьерж-сервис</h4>
      <div class="column buyPage-slide-subtile">
        <p class="medium-text-center text-serif">Вы хотите продать брендовые предметы своего гардероба, но боитесь, что продажа отнимет много времени? Для наших самых требовательных клиентов мы создали «Консьерж-сервис», который максимально упрощает процесс онлайн-продаж на сайте.</p>
      </div>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/sell-s2-delivery.svg"></div>
            <p>Оставьте запрос на сайте, и мы приедем к вам на помощь. Наша команда позаботится о бережном вывозе вещей в удобное для вас время и доставит их в наш офис.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/sell-s2-photo.svg"></div>
            <p>Мы создадим персональную карточку товара с подробным описанием вещи и профессиональными фото и опубликуем ее на сайте.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/sell-s2-guard.svg"></div>
            <p>Мы будем управлять вашими продажами,а вы – получать прибыль от них!</p>
          </div>
        </div>
      </div>
    </div>
    <div class="buyPage-slide">
      <h4 class="buyPage-slideTitle">Наши эксклюзивные услуги</h4>
      <p class="medium-text-center">Оригинальное происхождение и качество товара гарантированы: перед отправкой покупателю все товары проверяются нашими экспертами.</p>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-1.svg"></div>
            <p><strong>Приложение OSKELLY</strong></p>
            <p>Где бы вы ни находились – бренды всегда с вами! Совершайте покупки в мобильном приложении OSKELLY. Доступно в AppStore и Google Play.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-3.svg"></div>
            <p><strong>Сделайте предложение</strong></p>
            <p>Вы можете предложить свою цену продавцу. Покупайте любимые бренды по самым приятным ценам.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-4.svg"></div>
            <p><strong>VIP-сервис</strong></p>
            <p>Премиум-клиенты могут просматривать вещи до того, как они поступят в продажу, а также зарезервировать понравившиеся товары и получить бесплатную доставку для 12 заказов в год.</p>
          </div>
        </div>
      </div>
      <div class="row large-unstack">
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/sell-s3-cons.svg"></div>
            <p><strong>Консьерж</strong></p>
            <p>Доверьте продажи профессионалам</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/s3-5.svg"></div>
            <p><strong>Баллы и рейтинги</strong></p>
            <p>Зарабатывайте баллы на покупках и продажах и оплачивайте ими новые приобретения на OSKELLY.</p>
          </div>
        </div>
        <div class="small-12 large-4 column">
          <div class="welcomePage-slideFeature">
            <div class="welcomePage-slideFeature-icon"><img src="/images/icons/sell-s3-cleaning.svg"></div>
            <p><strong>Химчистка</strong></p>
            <p>Для вас есть услуга премиальной химчистки, качественная и они является нашим партнером</p>
          </div>
        </div>
      </div>
    </div>
    <p class="buyPage-helpText">Остались вопросы? Обратитесь в&nbsp;<a href="/info/kontakty">службу поддержки</a></p>
    <hr>
    <aside class="buyPage-nav">
      <ul>
        <li><a href="/info/welcome" class="buyPage-navLink"><span class="text-uppercase">Oskelly</span></a></li>
        <li><a href="/info/buy" class="buyPage-navLink">Покупайте</a></li>
        <li><a href="/info/sell" class="buyPage-navLink buyPage-navLink_active">Продавайте</a></li>
      </ul>
    </aside>
  </div>
</div>');
