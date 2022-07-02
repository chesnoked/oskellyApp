//
// Created by Виталий Хлудеев on 14.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class NotificationRow : UITableViewCell {

    private let message = UILabel()

    private var notification: Notification!
    private var controller: UIViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        addMessage()
        addSeparator()
    }

    private func addMessage() {
        contentView.addSubview(message)
        message.numberOfLines = 0
        message.font = MediumFont.systemFont(ofSize: 14)
        message.snp.makeConstraints({m in
            m.edges.equalTo(contentView).inset(UIEdgeInsetsMake(15, 10, 15, 10))
        })
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(contentView)
            m.right.equalTo(contentView)
            m.left.equalTo(contentView)
        }
    }

    func render(notification: Notification, controller: UIViewController) {
        self.notification = notification
        self.controller = controller
        message.text = (notification.createTime.map({return GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: $0, format: "dd.MM.yyyy HH:mm") + " "}) ?? "")
            + (notification.fullMessage ?? "")
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}