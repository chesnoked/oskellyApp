//
// Created by Виталий Хлудеев on 09.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductOfferController : UITableViewController {

    var parentController: ProductViewController?
    var product: Product!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.register(ProductOfferHeader.self, forCellReuseIdentifier: "ProductOfferHeader")
        tableView.register(ProductOfferButtonsRow.self, forCellReuseIdentifier: "ProductOfferButtonsRow")
        tableView.register(ProductOfferHistoryHeader.self, forCellReuseIdentifier: "ProductOfferHistoryHeader")
        tableView.register(ProductOfferHistoryRow.self, forCellReuseIdentifier: "ProductOfferHistoryRow")
        tableView.allowsSelection = false
        tableView.isUserInteractionEnabled = true
        navigationItem.title = "Переговорная площадка"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(section == 2) {
            return (product.offerRelated?.offersHistory?.count ?? 0) == 0 ? 0 : 1
        }
        return section == 3 ? (product.offerRelated?.offersHistory?.count ?? 0) : 1
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 4
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch(indexPath.section) {
            case 0:
                let cell = tableView.dequeueReusableCell(withIdentifier: "ProductOfferHeader", for: indexPath as IndexPath) as! ProductOfferHeader
                cell.render(product: product)
                return cell
            case 1:
                let cell = tableView.dequeueReusableCell(withIdentifier: "ProductOfferButtonsRow", for: indexPath as IndexPath) as! ProductOfferButtonsRow
                cell.render(product: product, controller: self)
                return cell
            case 2:
                let cell = tableView.dequeueReusableCell(withIdentifier: "ProductOfferHistoryHeader", for: indexPath as IndexPath) as! ProductOfferHistoryHeader
                return cell
            default:
                let cell = tableView.dequeueReusableCell(withIdentifier: "ProductOfferHistoryRow", for: indexPath as IndexPath) as! ProductOfferHistoryRow
                cell.render(product: product, offerHistory: product.offerRelated?.offersHistory?[indexPath.row])
                return cell
        }
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }

    override func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}