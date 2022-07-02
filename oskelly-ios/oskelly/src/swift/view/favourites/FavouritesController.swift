//
// Created by Виталий Хлудеев on 01.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class FavouritesController : ProductCollectionViewController {

    var profileId : Int!

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Фавориты"
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        self.existsMore = false
        GlobalProvider.instance.getApiRequester().getFavourites(page: currentPage, profileId: profileId) { page in
            completionHandler(page.items!)
        }
    }
}