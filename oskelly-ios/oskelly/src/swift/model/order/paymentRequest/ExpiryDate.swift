//
//  ExpiryDate.swift
//
//  Created by Виталий Хлудеев on 01.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ExpiryDate: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let year = "year"
    static let month = "month"
  }

  // MARK: Properties
  public var year: Int?
  public var month: Int?

  init(){}

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    year = json[SerializationKeys.year].int
    month = json[SerializationKeys.month].int
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = year { dictionary[SerializationKeys.year] = value }
    if let value = month { dictionary[SerializationKeys.month] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.year = aDecoder.decodeObject(forKey: SerializationKeys.year) as? Int
    self.month = aDecoder.decodeObject(forKey: SerializationKeys.month) as? Int
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(year, forKey: SerializationKeys.year)
    aCoder.encode(month, forKey: SerializationKeys.month)
  }

}
