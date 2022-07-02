//
// Created by Виталий Хлудеев on 16.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountFollowingsController : UITableViewController {

    var profile: PublicProfile?
    var parentController : ProfileProductsController?
    var isFollowings: Bool = true

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.isUserInteractionEnabled = true
        tableView.register(AccountFollowingRow.self, forCellReuseIdentifier: "AccountFollowingRow")
        navigationItem.title = isFollowings ? "Подписки" : "Подписчики"
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(isFollowings) {
            return profile?.followingsCount ?? 0
        }
        else {
            return profile?.followersCount ?? 0
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "AccountFollowingRow", for: indexPath) as! AccountFollowingRow
        if(isFollowings) {
            cell.render(following: profile?.followings?[indexPath.row], controller: self)
        }
        else {
            cell.render(following: profile?.followers?[indexPath.row], controller: self)
        }
        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
}