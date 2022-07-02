//
// Created by Виталий Хлудеев on 11.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogSearchController : ProductCollectionViewController {

    var search : String!

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Поиск"
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        self.existsMore = false
        GlobalProvider.instance.getApiRequester().search(search: search, completionHandler: {page in
            completionHandler(page.items!)
        })
    }
}