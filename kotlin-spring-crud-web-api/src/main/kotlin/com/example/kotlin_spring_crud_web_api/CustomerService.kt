package com.example.kotlin_spring_crud_web_api

import org.springframework.stereotype.Service

/**
 * Customer 操作をおこなうメソッドをまとめるインタフェース
 *
 */
interface CustomerService {
    /**
     * Customer 追加
     *
     * @param firstName
     * @param lastName
     */
    fun insertCustomer(firstName: String, lastName: String)
    /**
     * Customer 取得
     *
     * @return
     */
    fun selectCustomer(): List<Customer>

    /**
     * Customer 更新
     *
     * @param id
     * @param firstName
     * @param lastName
     */
    fun updateCustomer(id: Int, firstName: String, lastName: String)

    /**
     * Customer 削除
     *
     * @param id
     */
    fun deleteCustomer(id: Int)
}

/**
 * Customer 操作をおこなうメソッドを実装するクラス
 *
 * @property customerRepository
 */
@Service
class CustomerServiceImpl(val customerRepository: CustomerRepository) : CustomerService {
    override fun insertCustomer(firstName: String, lastName: String) {
        customerRepository.add(firstName, lastName)
        return
    }

    override fun selectCustomer(): List<Customer> {
        return customerRepository.find()
    }

    override fun updateCustomer(id: Int, firstName: String, lastName: String) {
        customerRepository.update(id, firstName, lastName)
        return
    }

    override fun deleteCustomer(id: Int) {
        customerRepository.delete(id)
        return
    }
}
