//
// Created by Виталий Хлудеев on 25.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class BaseStepController : UIViewController {

    let tableView = UITableView()

    var draft: Draft!

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.tableFooterView = UIView()
        tableView.separatorStyle = .none
        tableView.backgroundColor = AppColors.transparent()
        self.view.addSubview(tableView)
        tableView.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(view.snp.edges)
        }
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        tableView.register(SubmitAttributeRow.self, forCellReuseIdentifier: "Row")
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        draft = GlobalProvider.instance.draftProvider.getCurrent()
        self.tableView.reloadData()
    }
}