//
//  Images.swift
//
//  Created by Виталий Хлудеев on 20.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Images: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let url = "url"
    static let order = "order"
  }

  // MARK: Properties
  public var url: String?
  public var order: Int?

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
    if let value = json[SerializationKeys.url].string { url = ApiRequester.domain + value }
    order = json[SerializationKeys.order].int
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = url { dictionary[SerializationKeys.url] = value }
    if let value = order { dictionary[SerializationKeys.order] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.url = aDecoder.decodeObject(forKey: SerializationKeys.url) as? String
    self.order = aDecoder.decodeObject(forKey: SerializationKeys.order) as? Int
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(url, forKey: SerializationKeys.url)
    aCoder.encode(order, forKey: SerializationKeys.order)
  }

}
