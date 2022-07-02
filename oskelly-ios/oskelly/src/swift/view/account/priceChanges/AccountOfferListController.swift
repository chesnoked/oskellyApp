//
// Created by Виталий Хлудеев on 09.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOfferListController : UITableViewController {

    var offers : [AccountOffer] = []
    var offersToMe = false

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(AccountOfferRow.self, forCellReuseIdentifier: "Row")
        tableView.separatorStyle = .none
        loadOffers()
        navigationItem.title = "Мне предложили цену"
        GlobalProvider.instance.cartIconService.addCartIcon(self)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return offers.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath as IndexPath) as! AccountOfferRow
        cell.render(offer: offers[indexPath.row], controller: self)
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }

    func loadOffers() {
        GlobalProvider.instance.getApiRequester().getAccountOffers(offersToMe: offersToMe, completeHandler: { offers in
            self.offers = offers
            self.tableView.reloadData()
        })
    }
}