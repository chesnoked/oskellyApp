//
//  ProductSizeValue.swift
//
//  Created by Виталий Хлудеев on 06.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ProductSizeValue: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let id = "id"
    static let lowestPrice = "lowestPrice"
    static let value = "value"
  }

  // MARK: Properties
  public var id: Int?
  public var lowestPrice: Float?
  public var value: String?

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
    id = json[SerializationKeys.id].int
    lowestPrice = json[SerializationKeys.lowestPrice].float
    value = json[SerializationKeys.value].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = lowestPrice { dictionary[SerializationKeys.lowestPrice] = value }
    if let value = value { dictionary[SerializationKeys.value] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.lowestPrice = aDecoder.decodeObject(forKey: SerializationKeys.lowestPrice) as? Float
    self.value = aDecoder.decodeObject(forKey: SerializationKeys.value) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(lowestPrice, forKey: SerializationKeys.lowestPrice)
    aCoder.encode(value, forKey: SerializationKeys.value)
  }

}
