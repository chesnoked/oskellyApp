//
// Created by Виталий Хлудеев on 11.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountNewsController : UITableViewController {

    var notifications : [Notification] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        GlobalProvider.instance.cartIconService.addCartIcon(self)
        tableView.separatorStyle = .none
        tableView.isUserInteractionEnabled = true
        tableView.allowsSelection = true
        tableView.register(AccountNewsRow.self, forCellReuseIdentifier: "AccountNewsRow")
        navigationItem.title = "Новости"
        loadNotifications()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return notifications.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "AccountNewsRow", for: indexPath) as! AccountNewsRow
        cell.render(notification: notifications[indexPath.row], controller: self)
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        GlobalProvider.instance.navigator.navigateToNotificationTarget(notification: notifications[indexPath.row], controller: self)
    }

    func loadNotifications() {
        GlobalProvider.instance.getApiRequester().getMyNews(completeHandler: {notifications in
            self.notifications = notifications
            self.tableView.reloadData()
        })
    }
}