//
// Created by Виталий Хлудеев on 17.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import ImageSlideshow
import SnapKit

class HomePageHeaderViewCell : UICollectionViewCell {

    let slideshow: ImageSlideshow = ImageSlideshow()
    let newArrivalsLabel = UILabel()
    let newArrivalsCountLabel = UILabel()

    override init(frame: CGRect) {
        super.init(frame: frame)

        slideshow.backgroundColor = UIColor.white
        slideshow.slideshowInterval = 5.0
        slideshow.pageControlPosition = PageControlPosition.insideScrollView
        slideshow.pageControl.currentPageIndicatorTintColor = UIColor.white
        slideshow.pageControl.pageIndicatorTintColor = UIColor.lightGray
        slideshow.contentScaleMode = UIViewContentMode.scaleAspectFill
        slideshow.activityIndicator = DefaultActivityIndicator()

        slideshow.frame = self.frame
        self.contentView.addSubview(slideshow)
        slideshow.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp.top)
            make.width.equalTo(self.snp.width)
            make.height.equalTo(self.snp.height).multipliedBy(0.85)
        }

        let labelsView = UIView()
        labelsView.backgroundColor = UIColor.white
        self.contentView.addSubview(labelsView)
        labelsView.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(self.snp.width)
            make.height.equalTo(self.snp.height).multipliedBy(0.15)
            make.top.equalTo(slideshow.snp.bottom)
        }

        let stripeView = UIView()
        stripeView.backgroundColor = UIColor.lightGray
        self.contentView.addSubview(stripeView)
        stripeView.snp.makeConstraints { (make) -> Void in
            make.bottom.equalTo(labelsView.snp.bottom)
            make.centerX.equalTo(labelsView.snp.centerX)
            make.width.equalTo(self.snp.width).multipliedBy(0.9)
            make.height.equalTo(1)
        }

        newArrivalsLabel.textAlignment = .center
        newArrivalsLabel.text = "Новые поступления"
        newArrivalsLabel.font = MediumFont.systemFont(ofSize: 16)
        labelsView.addSubview(newArrivalsLabel)
        newArrivalsLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(labelsView.snp.centerY)
            make.left.equalTo(stripeView.snp.left)
        }

        contentView.addSubview(newArrivalsCountLabel)
        newArrivalsCountLabel.font = newArrivalsLabel.font
        newArrivalsCountLabel.textColor = .gray
        newArrivalsCountLabel.snp.makeConstraints({ m in
            m.centerY.equalTo(newArrivalsLabel)
            m.right.equalTo(stripeView)
        })
    }

    func render(urls: [String], itemsCount: Int?) {
        let alamofireSource = urls.map {(url) -> AlamofireSource in
            return AlamofireSource(urlString: url)!
        }
        slideshow.setImageInputs(alamofireSource)

        newArrivalsCountLabel.text = itemsCount.map({ " (" + String($0) + ")"})
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
