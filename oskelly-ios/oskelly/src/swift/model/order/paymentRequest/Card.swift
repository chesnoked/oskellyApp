//
//  Card.swift
//
//  Created by Виталий Хлудеев on 01.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Card: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let cvc2 = "cvc2"
    static let number = "number"
    static let expiryDate = "expiry_date"
  }

  // MARK: Properties
  public var cvc2: String?
  public var number: String?
  public var expiryDate: ExpiryDate?

  init() {}

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
    cvc2 = json[SerializationKeys.cvc2].string
    number = json[SerializationKeys.number].string
    expiryDate = ExpiryDate(json: json[SerializationKeys.expiryDate])
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = cvc2 { dictionary[SerializationKeys.cvc2] = value }
    if let value = number { dictionary[SerializationKeys.number] = value }
    if let value = expiryDate { dictionary[SerializationKeys.expiryDate] = value.dictionaryRepresentation() }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.cvc2 = aDecoder.decodeObject(forKey: SerializationKeys.cvc2) as? String
    self.number = aDecoder.decodeObject(forKey: SerializationKeys.number) as? String
    self.expiryDate = aDecoder.decodeObject(forKey: SerializationKeys.expiryDate) as? ExpiryDate
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(cvc2, forKey: SerializationKeys.cvc2)
    aCoder.encode(number, forKey: SerializationKeys.number)
    aCoder.encode(expiryDate, forKey: SerializationKeys.expiryDate)
  }

}
