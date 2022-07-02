//
// Created by Виталий Хлудеев on 17.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProfileNewsController : AccountNewsController {

    var profile: PublicProfile?
    var parentController: ProfileProductsController?

    override func viewDidLoad() {
        super.viewDidLoad()
        parentController?.selectedIndex = 1
        navigationItem.title = "Профиль"
        (tableView as UITableView).backgroundColor = .white
        GlobalProvider.instance.cartIconService.addCartIcon(self)
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if let p = profile {
            let header = ProfileHeader()
            header.render(profile: p, controller: parentController, profileNewsController: self)
            return header
        }
        return super.tableView(tableView, viewForFooterInSection: section)
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 150 + ProfileHeader.scrollViewHeight
    }

    func switchTab(selectedIndex: Int) {
        switch selectedIndex {
            case 1:
                return
            default:
                let c = storyboard!.instantiateViewController(withIdentifier: "ProfileProductsController") as! ProfileProductsController
                c.profile = profile
                c.selectedIndex = selectedIndex
                navigationController?.pushViewController(c, animated: false)
                navigationController.map({
                    $0.viewControllers.remove(at: $0.viewControllers.count - 2)
                })
        }
    }

    override func loadNotifications() {
        profile.map({
            GlobalProvider.instance.getApiRequester().getNews(profileId: $0.id, completeHandler: {notifications in
                self.notifications = notifications
                self.tableView.reloadData()
            })
        })
    }
}