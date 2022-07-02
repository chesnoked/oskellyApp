//
// Created by Виталий Хлудеев on 10.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountController: UITableViewController {

    let items = [
        "Редактировать профиль",
        "Мои товары",
        "Мои продажи",
        "Мои заказы",
        "Мой Wish List",
        "Мои Фавориты",
        "Ценовые изменения",
        "Новости",
        "Блог",
        "Выход"
    ]

    var account : Account?

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CatalogRow.self, forCellReuseIdentifier: "Row")
        tableView.register(AccountHeaderRow.self, forCellReuseIdentifier: "Header")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Мой профиль"
        GlobalProvider.instance.cartIconService.addCartIcon(self)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if(account == nil) {
            tableView.reloadData()
        }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
            case 0:
                return 1
            default:
                return items.count
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.section {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Header", for: indexPath as IndexPath) as! AccountHeaderRow
                GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                    cell.render(account: a, controller: self)
                    self.account = a
                }, needSynchronize: true)
                return cell
            default:
                let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! CatalogBasicRow
                cell.catalogNameLabel.text = items[indexPath.row]
                return cell
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
            case 0:
                return 132.0
            default:
                return 44.0
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        switch indexPath.section {
            case 1:
                switch indexPath.row {
                    case 0:
                        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                            let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountEditController") as! AccountEditController
                            c.account = a
                            self.navigationController?.pushViewController(c, animated: true)
                        }, needSynchronize: true)
                    case 1:
                        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                            let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountProductGroupsController") as! AccountProductGroupsController
                            c.account = a
                            self.navigationController?.pushViewController(c, animated: true)
                        }, needSynchronize: true)
                    case 2:
                        let c = self.storyboard!.instantiateViewController(withIdentifier: "SaleGroupsController") as! SaleGroupsController
                        self.navigationController?.pushViewController(c, animated: true)
                    case 3:
                        let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountOrderListController") as! AccountOrderListController
                        self.navigationController?.pushViewController(c, animated: true)
                    case 4:
                        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                            let c = self.storyboard!.instantiateViewController(withIdentifier: "WishListController") as! WishListController
                            c.profileId = a.id
                            self.navigationController?.pushViewController(c, animated: true)
                        }, needSynchronize: true)
                    case 5:
                        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                            let c = self.storyboard!.instantiateViewController(withIdentifier: "FavouritesController") as! FavouritesController
                            c.profileId = a.id
                            self.navigationController?.pushViewController(c, animated: true)
                        }, needSynchronize: true)
                    case 6:
                        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: { a in
                            let c = self.storyboard!.instantiateViewController(withIdentifier: "PriceChangesController") as! PriceChangesController
                            self.navigationController?.pushViewController(c, animated: true)
                        }, needSynchronize: true)
                    case 7:
                        let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountNewsController") as! AccountNewsController
                        self.navigationController?.pushViewController(c, animated: true)
                    case 8:
                        let c = self.storyboard!.instantiateViewController(withIdentifier: "BlogController") as! BlogController
                        self.navigationController?.pushViewController(c, animated: true)
                    case 9:
                        GlobalProvider.instance.getAccountManager().eraseCredentials()
                        let c = self.storyboard!.instantiateViewController(withIdentifier: "HelloViewController") as! HelloViewController
                        GlobalProvider.instance.accountProvider.setCurrent(account: nil)
                        self.present(c, animated: true)
                    default:
                        print("default")
                }
            default:
                return
        }
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
}