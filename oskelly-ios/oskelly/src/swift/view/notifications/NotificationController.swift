//
// Created by Виталий Хлудеев on 13.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class NotificationController : AccountNewsController {

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Оповещения"
        GlobalProvider.instance.cartIconService.addCartIcon(self)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        loadNotifications()
    }

    override func loadNotifications() {
        GlobalProvider.instance.getApiRequester().getMyNotifications(completeHandler: {notifications in
            self.notifications = notifications
            self.tableView.reloadData()
            GlobalProvider.instance.getApiRequester().readAllNotifications()
        })
    }
}