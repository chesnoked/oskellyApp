//
// Created by Виталий Хлудеев on 15.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountSingleOfferController : AccountOfferListController {

    var offerId : Int?

    override func loadOffers() {
        if offerId != nil {
            GlobalProvider.instance.getApiRequester().getAccountOffer(offerId: offerId!, completeHandler: { offer in
                self.offers = []
                self.offers.append(offer)
                self.tableView.reloadData()
            })
        }
    }
}
