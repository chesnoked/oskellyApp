//
// Created by Виталий Хлудеев on 08.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation

class DateTimeConverter {

    func fromDateTimeWithTimeZone(dateString: String, format: String) -> String {
        let dateFormatter = DateFormatter()
        let dateObj = fromDateTimeWithTimeZone(dateString: dateString)
        dateFormatter.timeZone = .current
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: dateObj)
    }

    func fromDate(dateString: String, format: String) -> String {
        let dateFormatter = DateFormatter()
        let dateObj = fromDate(dateString: dateString)
        dateFormatter.locale = Locale.init(identifier: "ru")
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: dateObj)
    }

    func fromDate(date: Date, format: String) -> String {
        let dateObj = date
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale.init(identifier: "ru")
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: dateObj)
    }

    func fromDateTimeWithTimeZone(dateString: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZZZZ"
        dateFormatter.locale = Locale.init(identifier: "en_US_POSIX")
        dateFormatter.timeZone = .current

        if let value = dateFormatter.date(from: dateString) {
            return value
        }
        else {
            let dateFormatter1 = DateFormatter()
            dateFormatter1.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ"
            dateFormatter1.locale = Locale.init(identifier: "en_US_POSIX")
            dateFormatter1.timeZone = .current
            return dateFormatter1.date(from: dateString)!
        }
    }

    func fromDate(dateString: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        dateFormatter.locale = Locale.init(identifier: "ru")
        dateFormatter.timeZone = .current
        return dateFormatter.date(from: dateString)!
    }
}