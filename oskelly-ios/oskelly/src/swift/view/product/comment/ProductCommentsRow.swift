//
// Created by Виталий Хлудеев on 08.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductCommentsRow : UITableViewCell {

    private let img = ProfilePhotoView()
    private let nick = UILabel()
    private let time = UILabel()
    private let commentText = UILabel()

    private var controller: UIViewController!
    private var comment: Comment!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        isUserInteractionEnabled = true
        addImg()
        addNick()
        addTime()
        addCommentText()
        addSeparator()
    }

    private func addCommentText() {
        contentView.addSubview(commentText)
        commentText.font = MediumFont.systemFont(ofSize: 14)
        commentText.numberOfLines = 100
//        commentText.text = "Привет"
        commentText.text = "В связи со смещением активных фронтальных разделов над территорией Приморского края вечером 8 января на юго-востоке, ночью и днем 9 января в восточных и центральных районах края местами ожидается сильный снег с количеством 6-15 мм за 12 часов и менее, сообщает Примгидромет. На северо–востоке Приморья местами пройдет очень сильный снег с количеством более 20 мм за 12 часов и менее. Ожидаются гололедные явления, на побережье местами метель при ветре 18-23 м/с. На дорогах гололедица, снежный накат."
        commentText.snp.makeConstraints({ m in
            m.left.equalTo(nick)
            m.right.equalTo(contentView).inset(10)
            m.top.equalTo(nick.snp.bottom).offset(5)
            m.bottom.equalTo(contentView).inset(24)
        })
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addTime() {
        contentView.addSubview(time)
        time.font = MediumFont.systemFont(ofSize: 13)
        time.textColor = .gray
//        time.text = "(01.01.2018 21:00)"
        time.snp.makeConstraints({m in
            m.centerY.equalTo(nick)
            m.left.equalTo(nick.snp.right).offset(5)
        })
    }

    private func addNick() {
        contentView.addSubview(nick)
        nick.font = BoldFont.systemFont(ofSize: 15)
        nick.text = "user"
        nick.snp.makeConstraints({m in
            m.top.equalTo(img).inset(3)
            m.left.equalTo(img.snp.right).offset(10)
        })
    }

    private func addImg() {
        contentView.addSubview(img)
        img.snp.makeConstraints({m in
            m.top.equalTo(contentView).inset(10)
            m.left.equalTo(contentView).inset(10)
            m.width.equalTo(60)
            m.height.equalTo(60)
        })
        img.layer.cornerRadius = 30
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        img.isUserInteractionEnabled = true
        img.addGestureRecognizer(tapGestureRecognizer)
    }

    func render(comment: Comment, controller: UIViewController) {
        self.controller = controller
        self.comment = comment
        commentText.text = (comment.text == nil || comment.text == "") ? " " : comment.text
        nick.text = comment.user
        if let v = comment.publishZonedDateTime {
            time.text = "(" + GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: v, format: "dd.MM.yyyy HH:mm") + ")"
        }
        else if(comment.publishTime != nil) {
            time.text = "(" + comment.publishTime! + ")"
        }
        img.setDefaultImage()
        if let v = comment.avatar {
            let url = URL(string: ApiRequester.domain + v)!
            img.af_setImage(withURL: url)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        if let v = comment.userId {
            GlobalProvider.instance.navigator.navigateToProfile(profileId: v, controller: controller)
        }
    }
}