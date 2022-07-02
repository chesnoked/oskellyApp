//
// Created by Виталий Хлудеев on 26.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitCategoryHeader : SubmitCategoryRow {

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.backgroundColor = AppColors.transparent()
        catalogNameLabel.font = MediumFont.systemFont(ofSize: 17)
        catalogNameLabel.textAlignment = .center
        catalogNameLabel.numberOfLines = 2
        catalogNameLabel.textColor = .darkGray
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
