//
// Created by Виталий Хлудеев on 02.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountProductGroupsController : UITableViewController {

    struct ProductGroup {
        let name : String
        let value : String?

        init(name: String, value: String?) {
            self.name = name
            self.value = value
        }
    }

    let items = [
//        ProductGroup(name: "Все", value: nil),
        ProductGroup(name: "Черновик", value: "DRAFT"),
        ProductGroup(name: "На модерации", value: "NEED_MODERATION"),
        ProductGroup(name: "Отклонен модератором", value: "REJECTED"),
        ProductGroup(name: "Опубликован", value: "PUBLISHED"),
        ProductGroup(name: "Скрыт", value: "HIDDEN"),
        ProductGroup(name: "Снят с продажи", value: "DELETED"),
    ]

    var account: Account?

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CatalogRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        GlobalProvider.instance.cartIconService.addCartIcon(self)
        self.navigationItem.title = "Мои товары"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! CatalogBasicRow
        cell.selectionStyle = .none
        cell.catalogNameLabel.text = items[indexPath.row].name
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44.0
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let group = items[indexPath.row]
        if(group.value == "DRAFT") {
            let c = self.storyboard?.instantiateViewController(withIdentifier: "DraftController")
            c.map({
                self.navigationController?.pushViewController($0, animated: true)
            })
        }
        else {
            var r = ProductRequest()
            r.state = group.value
            r.seller = account?.id

            let c = self.storyboard?.instantiateViewController(withIdentifier: "CatalogProductListController") as? CatalogProductListController
            c?.request = r
            c?.catalogName = group.name
            c.map({
                self.navigationController?.pushViewController($0, animated: true)
            })
        }
    }
}