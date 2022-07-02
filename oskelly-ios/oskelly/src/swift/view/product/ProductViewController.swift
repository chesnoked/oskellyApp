//
// Created by Виталий Хлудеев on 19.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import ImageSlideshow
import SnapKit

class ProductViewController : UITableViewController, CollapsibleTableViewHeaderDelegate {

    var productId: Int!
    var product: Product!
    var comments: [Comment] = []
    
    struct Section {
        var name: String!
        var collapsed: Bool!
        
        init(name: String, collapsed: Bool = false) {
            self.name = name
            self.collapsed = collapsed
        }
    }
    
    var sections = [Section]()

    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.register(SlideShowRow.self, forCellReuseIdentifier: "SlideShowRow")
        self.tableView.register(ProductDescriptionRow.self, forCellReuseIdentifier: "ProductDescriptionRow")
        self.tableView.register(BasketAndOfferButtonRow.self, forCellReuseIdentifier: "BasketAndOfferButtonRow")
        self.tableView.register(NotificationsRow.self, forCellReuseIdentifier: "NotificationsRow")
        self.tableView.register(UserInfoRow.self, forCellReuseIdentifier: "UserInfoRow")
        self.tableView.register(DescriptionSeparatorRow.self, forCellReuseIdentifier: "DescriptionSeparatorRow")
        self.tableView.register(ProductAttributeRow.self, forCellReuseIdentifier: "ProductAttributeRow")
        self.tableView.register(ProductAttributeRowHeader.self, forCellReuseIdentifier: "ProductAttributeRowHeader")
        self.tableView.register(ProductAttributeRowFooter.self, forCellReuseIdentifier: "ProductAttributeRowFooter")
        self.tableView.register(DeliveryRow.self, forCellReuseIdentifier: "DeliveryRow")
        self.tableView.register(ProductCommentsRow.self, forCellReuseIdentifier: "ProductCommentsRow")
        self.tableView.register(ProductAddCommentRow.self, forCellReuseIdentifier: "ProductAddCommentRow")
        self.tableView.separatorStyle = .none
        self.tableView.allowsSelection = false
        self.tableView.isUserInteractionEnabled = true

        let apiRequester = GlobalProvider.instance.getApiRequester()
        apiRequester.getProduct(productId: productId, completeHandler: { product in
            self.navigationItem.title = product.brand
            self.product = product
            self.tableView.reloadData()
            apiRequester.getProductComments(productId: self.productId, completeHandler: {comments in
                self.comments = comments
                self.tableView.reloadData()
            })
        })


        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
        
        sections = [
            Section(name: "ПОДРОБНЕЕ О ТОВАРЕ", collapsed: true),
            Section(name: "ДОСТАВКА", collapsed: true),
            Section(name: "ОПЛАТА", collapsed: true)
        ]

        GlobalProvider.instance.cartIconService.addCartIcon(self)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if(product == nil) {
            return // иначе возникает ошибка, т.к. экран не отрисован
        }
        let apiRequester = GlobalProvider.instance.getApiRequester()
        apiRequester.getProduct(productId: productId, completeHandler: { product in
            self.product = product
            (self.tableView as! UITableView).reloadRows(at: [IndexPath(row: 2, section: 0)], with: .none) // обновляем информацио об оффере
        })
    }

    public override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(product == nil) {
            return 0
        }
        if(section == 1) {
            return product.attributes!.count + 1 // футер временно выпиливаем
        }
        else if(section == 2 || section == 3) {
            return 1
        }
        else if(section == 4) {
            return 1
        }
        else if(section == 5) {
            return 1
        }
        else if(section == 6) {
            return comments.count
        }
        return 6
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if(section == 0 || section == 4) {
            return nil
        }
        let header = tableView.dequeueReusableHeaderFooterView(withIdentifier: "header") as? CollapsibleTableViewHeader ?? CollapsibleTableViewHeader(reuseIdentifier: "header")
        
        header.titleLabel.text = sections[section - 1].name
        header.setCollapsed(collapsed: sections[section - 1].collapsed)
        
        header.section = section
        header.delegate = self
        return header
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        if(product == nil) {
            return 0
        }
        return 7
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if(section > 0 && section < 4) {
            return 44.0
        }
        return 0
    }

    public override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if(indexPath.section == 1 && indexPath.row == 0) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProductAttributeRowHeader") as! ProductAttributeRowHeader
            return cell
        }
        else if(indexPath.section == 1 && indexPath.row == product.attributes!.count + 1) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProductAttributeRowFooter") as! ProductAttributeRowFooter
            return cell
        }
        else if(indexPath.section == 1) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProductAttributeRow") as! ProductAttributeRow
            cell.render(productAttribute: product.attributes![indexPath.row - 1])
            return cell
        }
        else if(indexPath.section == 2) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "DeliveryRow") as! DeliveryRow
            cell.render(text: "После оформления вашего заказа, продавец подтверждает сделку и отправляет товар на экспертизу OSKELLY. После подтверждения подлинности и качества товара, товар будет отправлен вам. Вы можете отслеживать статусы доставки в личном кабинете.\n\nПроцесс доставки состоит из двух этапов: от продавца до офиса OSKELLY и от офиса OSKELLY до покупателя. Расчет стоимости и сроков доставки проходит индивидуально и зависит от того, насколько далеко продавец находится от покупателя.\n\nЕсли стоимость товара превышает 25 000 рублей доставка этого товара осуществляется бесплатно.")
            return cell
        }
        else if(indexPath.section == 3) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "DeliveryRow") as! DeliveryRow
            cell.render(text: "При совершении покупки деньги на банковском счете покупателя замораживаются. Они не спишутся до тех пор, пока изделие не пройдет аутентификацию.\n\nПеревод денег продавцу осуществляется после доставки товара клиенту. Это является гарантией качества сделки и, в частности, гарантией добросовестности продавца.")
            return cell
        }
        else if(indexPath.section == 4) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "DescriptionSeparatorRow") as! DescriptionSeparatorRow
            cell.topViewSeparotor.isHidden = true
            return cell
        }
        else if(indexPath.section == 5) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProductAddCommentRow") as! ProductAddCommentRow
            cell.render(controller: self, product: product)
            return cell
        }
        else if(indexPath.section == 6) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProductCommentsRow") as! ProductCommentsRow
            cell.render(comment: comments[indexPath.row], controller: self)
            return cell
        }
        
        var reuseIdentifier = "SlideShowRow"
        switch (indexPath.row) {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "SlideShowRow", for: indexPath as IndexPath) as! SlideShowRow
                cell.render(product: product)
                return cell
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "ProductDescriptionRow", for: indexPath as IndexPath) as! ProductDescriptionRow
                cell.render(product: product)
                return cell
            case 2:
                let cell = tableView.dequeueReusableCell(withIdentifier: "BasketAndOfferButtonRow", for: indexPath as IndexPath) as! BasketAndOfferButtonRow
                cell.render(product: product, controller: self)
                cell.contentView.isUserInteractionEnabled = true
                return cell
            case 3: reuseIdentifier = "NotificationsRow"
                let cell = tableView.dequeueReusableCell(withIdentifier: "NotificationsRow", for: indexPath as IndexPath) as! NotificationsRow
                cell.render(product: product, controller: self)
                return cell
            case 4:
                let cell = tableView.dequeueReusableCell(withIdentifier: "UserInfoRow", for: indexPath as IndexPath) as! UserInfoRow
                cell.render(product: product, controller: self)
                cell.contentView.isUserInteractionEnabled = true
                return cell
            case 5:
                let cell = tableView.dequeueReusableCell(withIdentifier: "DescriptionSeparatorRow") as! DescriptionSeparatorRow
                cell.topViewSeparotor.isHidden = false
                return cell
            default: reuseIdentifier = "SlideShowRow"
        }
        return tableView.dequeueReusableCell(withIdentifier: reuseIdentifier, for: indexPath as IndexPath)
    }

    public override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        if(indexPath.section == 1 && indexPath.row == 0) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 3.5
        }
        else if(indexPath.section == 1 && indexPath.row == product.attributes!.count + 1) {
//            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 2
            return 0
        }
        else if(indexPath.section == 1) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0
        }
        else if(indexPath.section == 2) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 6
        }
        else if(indexPath.section == 3) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 3
        }
        else if(indexPath.section == 4) {
            return UITableViewAutomaticDimension
        }
        return UITableViewAutomaticDimension
    }

    public override func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        if(indexPath.section == 1 && indexPath.row == 0) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 3.5
        }
        else if(indexPath.section == 1 && indexPath.row == product.attributes!.count + 1) {
//            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 2
            return 0
        }
        else if(indexPath.section == 1) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0
        }
        else if(indexPath.section == 2) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 6
        }
        else if(indexPath.section == 3) {
            return sections[indexPath.section - 1].collapsed! ? 0 : 44.0 * 3
        }
        else if(indexPath.section == 4) {
            return 400
        }
        return 400
    }

    func toggleSection(header: CollapsibleTableViewHeader, section: Int) {
        
        if(section == 1) {
            let collapsed = !sections[section - 1].collapsed
            sections[section - 1].collapsed = collapsed
            header.setCollapsed(collapsed: collapsed)
            tableView.beginUpdates()
            for i in 0 ..< product.attributes!.count + 1 {
                tableView.reloadRows(at: [NSIndexPath.init(row: i, section: section) as IndexPath], with: .automatic)
            }
            tableView.endUpdates()
            return
        }
        
        else if(section == 2 || section == 3) {
            let collapsed = !sections[section - 1].collapsed
            sections[section - 1].collapsed = collapsed
            header.setCollapsed(collapsed: collapsed)
            tableView.beginUpdates()
            tableView.reloadRows(at: [NSIndexPath.init(row: 0, section: section) as IndexPath], with: .automatic)
            tableView.endUpdates()
            return
        }
    }
}


