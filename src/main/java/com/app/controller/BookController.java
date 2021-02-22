package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.app.model.Book;
import com.app.repository.BookRepository;

@Controller
public class BookController {
	
	// repositoryインターフェースを自動インスタンス化
	@Autowired
	private BookRepository bookstore;
	
	/**
	 * [list] へアクセスがあった場合
	 */
	@RequestMapping("/list")
	public ModelAndView list(ModelAndView mav) {
		// bookstoreテーブルから全件取得
		Iterable<Book> book_list = bookstore.findAll();
		
		// Viewに渡す変数をModelに格納
		mav.addObject("book_list", book_list);
		
		// 画面に出力するViewを指定
		mav.setViewName("list");
		
		// return Model and View
		return mav;
	}
	
	
	/**
	 * [index]へアクセスがあった場合
	 */
	@RequestMapping("/insert")
	public ModelAndView index(@ModelAttribute Book book, ModelAndView mav) {
		// store various for View to Model
		mav.addObject("book", book);
		
		// 画面に出力するViewを指定
		mav.setViewName("insert");
		
		// return Model and View
		return mav;
	}
	
	/**
	 * [insert]へPOST送信された場合
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	// POSTデータをBookインスタンスとして受け取る
	public ModelAndView insertPost(@ModelAttribute @Validated Book book, BindingResult result, ModelAndView mav) {
		
		// if input error exist
		if(result.hasErrors()) {
			// error message
			mav.addObject("message", "入力内容に誤りがあります");
			
			//　画面に出力するViewを指定
			mav.setViewName("insert");
			
			// return Model and View
			return mav;
		}
		
		
		// store to db input data;
		bookstore.saveAndFlush(book);
		
		// redirect
		mav = new ModelAndView("redirect:/list");
		
		// return Model and View
		return mav;
	}
}
