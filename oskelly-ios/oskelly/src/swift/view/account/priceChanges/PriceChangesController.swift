//
// Created by Виталий Хлудеев on 08.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PriceChangesController : UITableViewController {

    var items : [RowWithAction] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(CatalogRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        self.navigationItem.title = "Ценовые изменения"
        GlobalProvider.instance.cartIconService.addCartIcon(self)

        items = [
            RowWithAction(key: "Слежу за ценой", action: {
                let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountPriceFollowingController") as! AccountPriceFollowingController
                c.navigationItem.title = "Слежу за ценой"
                self.navigationController?.pushViewController(c, animated: true)
            }),
            RowWithAction(key: "Я предложил цену", action: {
                let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountOfferListController") as! AccountOfferListController
                c.offersToMe = false
                c.navigationItem.title = "Я предложил цену"
                self.navigationController?.pushViewController(c, animated: true)
            }),
            RowWithAction(key: "Мне предложили цену", action: {
                let c = self.storyboard!.instantiateViewController(withIdentifier: "AccountOfferListController") as! AccountOfferListController
                c.offersToMe = true
                c.navigationItem.title = "Мне предложили цену"
                self.navigationController?.pushViewController(c, animated: true)
            })
        ]
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! CatalogBasicRow
        cell.catalogNameLabel.text = items[indexPath.row].key
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        items[indexPath.row].action()
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
}